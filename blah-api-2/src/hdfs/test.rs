use super::channel::ChannelFactory;

#[test]
fn test() {
    let mut chan = ChannelFactory::new("127.0.0.1:8020").unwrap();
    chan.open();
    chan.send();
    println!("test");
}
