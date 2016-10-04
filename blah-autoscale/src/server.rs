
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
        let mut poll = try!(Poll::new());

        poll.register(&rx, MESSAGE, Ready::readable(),
                      PollOpt::edge()).unwrap();
        poll.register(&socket, SERVER, Ready::all(),
                      PollOpt::edge()).unwrap();

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
            self.poll.poll(&mut events, None).unwrap();
            for event in events.iter() {
                match event.token() {
                    MESSAGE => self.handle_message(),
                    SERVER => self.handle_conn(),
                    id @ _ => self.handle_req(id),
                }
            }
        }

        Ok(())
    }

    fn handle_conn(&mut self) {
        if let Ok((mut stream, _)) = self.socket.accept() {
            self.poll.register(&stream,
                               Token(self.next_client),
                               Ready::all(),
                               PollOpt::edge()).unwrap();
            stream.write(b"HTTP/1.1 200 OK\r\n");
            stream.write(b"Content-Type: text/event-stream\r\n");
            stream.write(b"Cache-Control: no-cache\r\n");
            stream.write(b"Access-Control-Allow-Origin: *\r\n\r\n");
            self.clients.insert(self.next_client, stream);
            self.next_client += 1;
        }
    }

    fn handle_message(&mut self) {
        let msg = self.rx.try_recv();
        for (id, mut client) in &self.clients {
            client.write(b"data: Test\n\n");
        }
    }

    fn handle_req(&mut self, token: Token) {
        println!("{:?}", token);
    }
}
