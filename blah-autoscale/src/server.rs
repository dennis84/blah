
use std::io::{self};
use mio::*;
use mio::tcp::{TcpListener, TcpStream};
use mio::channel::{channel, Sender, Receiver};
use std::collections::HashMap;
use std::io::{Write};

const MESSAGE: Token = Token(0);
const SERVER: Token = Token(1);

pub struct Server {
    socket: TcpListener,
    rx: Receiver<String>,
    clients: HashMap<usize, TcpStream>,
    next_client: usize,
    poll: Poll,
}

impl Server {
    pub fn new() -> io::Result<(Sender<String>, Server)> {
        let (tx, rx) = channel::<String>();
        let addr = "127.0.0.1:8003".parse().unwrap();
        let socket = try!(TcpListener::bind(&addr));
        let poll = try!(Poll::new());

        try!(poll.register(&rx, MESSAGE, Ready::readable(),
                           PollOpt::edge()));
        try!(poll.register(&socket, SERVER, Ready::all(),
                           PollOpt::edge()));

        Ok((tx, Server {
            socket: socket,
            rx: rx,
            clients: HashMap::new(),
            next_client: 2,
            poll: poll,
        }))
    }

    pub fn start(&mut self) -> io::Result<()> {
        let mut events = Events::with_capacity(1024);
        loop {
            try!(self.poll.poll(&mut events, None));
            for event in events.iter() {
                match event.token() {
                    MESSAGE => self.handle_message().unwrap_or_else(|e| {
                        debug!("Error during message handling: {}", e);
                    }),
                    SERVER => self.handle_conn().unwrap_or_else(|e| {
                        debug!("Error during connection handling: {}", e);
                    }),
                    _ => {},
                }
            }
        }

        Ok(())
    }

    fn handle_conn(&mut self) -> io::Result<()> {
        if let Ok((mut stream, _)) = self.socket.accept() {
            try!(self.poll.register(&stream,
                                    Token(self.next_client),
                                    Ready::all(),
                                    PollOpt::edge()));
            try!(stream.write(b"HTTP/1.1 200 OK\r\n"));
            try!(stream.write(b"Content-Type: text/event-stream\r\n"));
            try!(stream.write(b"Cache-Control: no-cache\r\n"));
            try!(stream.write(b"Access-Control-Allow-Origin: *\r\n\r\n"));
            self.clients.insert(self.next_client, stream);
            self.next_client += 1;
        }

        Ok(())
    }

    fn handle_message(&mut self) -> io::Result<()> {
        if let Ok(msg) = self.rx.try_recv() {
            let data = format!("data: {}\n\n", msg);
            let bytes = data.as_bytes();
            for (_, mut client) in &self.clients {
                try!(client.write(bytes));
            }
        }

        Ok(())
    }
}
