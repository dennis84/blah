use super::channel::ChannelFactory;
use super::client::Client;

#[test]
fn test() {
    let mut chan = ChannelFactory::new("127.0.0.1:8020").unwrap();
    chan.open();
    chan.mkdir();
    println!("test");
}
