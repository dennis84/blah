use super::channel::{ChannelFactory};
use super::client::{Client};

#[test]
fn test() {
    let mut chan = ChannelFactory::new("127.0.0.1:8020").unwrap();
    chan.mkdir("/test").unwrap();
    chan.rm("/test").unwrap();
    chan.touch("/foo.txt").unwrap();
    chan.rm("/foo.txt").unwrap();
    // let mut writer = chan.create("/test.txt");
    // writer.write(b"hello");
    // writer.write(b"world");
    // writer.close();

    println!("test");
}
