use super::channel::{ChannelFactory};
use super::client::{Client};

#[test]
fn test() {
    let mut chan = ChannelFactory::new("127.0.0.1:8020").unwrap();
    match chan.mkdir("/test") {
        Ok(resp) => { println!("{:?}", resp) }
        Err(e) => { println!("{:?}", e) }
    }

    match chan.mkdir("/test2") {
        Ok(resp) => { println!("{:?}", resp) }
        Err(e) => { println!("{:?}", e) }
    }

    // chan.rmdir("/test");
    // chan.touch("/foo.txt");
    // let mut writer = chan.create("/test.txt");
    // writer.write(b"hello");
    // writer.write(b"world");
    // writer.close();

    println!("test");
}
