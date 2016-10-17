use std::io::{self, Write, Read};
use std::collections::HashMap;
use mio::*;
use mio::tcp::{TcpListener, TcpStream, Shutdown};
use mio::channel::{channel, Sender, Receiver};
use httparse;

const MESSAGE: Token = Token(0);
const SERVER: Token = Token(1);

pub struct Server {
    socket: TcpListener,
    rx: Receiver<String>,
    clients: HashMap<Token, TcpStream>,
    next_client: usize,
    poll: Poll,
}

impl Server {
    pub fn new() -> io::Result<(Sender<String>, Server)> {
        let (tx, rx) = channel::<String>();
        let addr = "0.0.0.0:8003".parse().unwrap();
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
        println!("Start server");
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
                    token => self.handle_req(event, token).unwrap_or_else(|e| {
                        debug!("Error during req: {}", e);
                    }),
                }
            }
        }
    }

    fn handle_conn(&mut self) -> io::Result<()> {
        if let Ok((stream, _)) = self.socket.accept() {
            let token = Token(self.next_client);
            self.clients.insert(token, stream);
            try!(self.poll.register(&self.clients[&token],
                                    token, Ready::readable(),
                                    PollOpt::edge() | PollOpt::oneshot()));
            self.next_client += 1;
        }

        Ok(())
    }

    fn handle_message(&mut self) -> io::Result<()> {
        if let Ok(msg) = self.rx.try_recv() {
            debug!("Nb clients: {}", self.clients.len());
            let data = format!("data: {}\n\n", msg);
            let bytes = data.as_bytes();
            for (_, mut client) in &self.clients {
                try!(client.write(bytes));
            }
        }

        Ok(())
    }

    fn handle_req(&mut self, event: Event, token: Token) -> io::Result<()> {
        if event.kind().is_readable() {
            let mut buf = [0u8; 1024];
            let mut headers = [httparse::EMPTY_HEADER; 16];
            let mut req = httparse::Request::new(&mut headers);

            if let Some(stream) = self.clients.get_mut(&token) {
                match stream.read(&mut buf) {
                    Err(_) => {},
                    Ok(bytes_read) => {
                        match req.parse(&buf[..bytes_read]) {
                            Ok(_) => {},
                            Err(e) => error!("Cannot parse request: {:?}", e),
                        };
                    }
                }

                match (req.method, req.path) {
                    (Some("GET"), Some("/healthcheck")) => {
                        try!(stream.write(b"HTTP/1.1 200 OK\r\n"));
                        try!(stream.write(b"Content-Length: 7\r\n"));
                        try!(stream.write(b"Connection: close\r\n\r\n"));
                        try!(stream.write(b"healthy"));
                    },
                    (Some("GET"), Some("/")) => {
                        try!(stream.write(b"HTTP/1.1 200 OK\r\n"));
                        try!(stream.write(b"Content-Type: text/event-stream\r\n"));
                        try!(stream.write(b"Cache-Control: no-cache\r\n"));
                        try!(stream.write(b"Access-Control-Allow-Origin: *\r\n"));
                        try!(stream.write(b"Access-Control-Headers: *\r\n\r\n"));
                    },
                    (_, _) => {
                        try!(stream.write(b"HTTP/1.1 404 OK\r\n\r\n"));
                    },
                }
            }

            match (req.method, req.path) {
                (Some("GET"), Some("/")) => {},
                (_, _) => try!(self.close_connection(&token)),
            }
        }

        if event.kind().is_error() || event.kind().is_hup() {
            try!(self.close_connection(&token));
        }

        Ok(())
    }

    fn close_connection(&mut self, token: &Token) -> io::Result<()> {
        if let Some(stream) = self.clients.get_mut(&token) {
            try!(self.poll.deregister(stream));
            try!(stream.shutdown(Shutdown::Both));
        }

        self.clients.remove(&token);
        Ok(())
    }
}
