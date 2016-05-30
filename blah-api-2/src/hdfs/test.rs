use super::channel::{ChannelFactory};
use super::client::{Client};

#[test]
fn test() {
    let mut chan = ChannelFactory::new("127.0.0.1:8020").unwrap();
    chan.mkdir("/test").unwrap();
    chan.rm("/test").unwrap();
    chan.touch("/foo.txt").unwrap();
    chan.rm("/foo.txt").unwrap();
    let file = chan.create("/test.txt").unwrap();
    chan.write(&file, b"hello").unwrap();
    chan.write(&file, b"world").unwrap();
    chan.close(&file).unwrap();
    chan.cat("/test.txt").unwrap();
    chan.rm("/test.txt").unwrap();
}
