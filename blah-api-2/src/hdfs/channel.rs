use std::io::prelude::*;
use std::net::TcpStream;
use byteorder::{BigEndian, WriteBytesExt};

pub struct ChannelFactory;

impl ChannelFactory {
    pub fn new(addr: &str) -> Result<Channel, String> {
        let stream = TcpStream::connect(addr).unwrap();
        Ok(Channel {
            stream: stream,
        })
    }
}

pub struct Channel {
    stream: TcpStream,
}

impl Channel {
    /// Writes the Hadoop headers.
    ///
    /// +---------------------------------------------------------------------+
    /// |  Header, 4 bytes ("hrpc")                                           |
    /// +---------------------------------------------------------------------+
    /// |  Version, 1 byte (default verion 9)                                 |
    /// +---------------------------------------------------------------------+
    /// |  RPC service class, 1 byte (0x00)                                   |
    /// +---------------------------------------------------------------------+
    /// |  Auth protocol, 1 byte (Auth method None = 0)                       |
    /// +---------------------------------------------------------------------+
    /// |  Length of the RpcRequestHeaderProto  + length of the               |
    /// |  of the IpcConnectionContextProto (4 bytes/32 bit int)              |
    /// +---------------------------------------------------------------------+
    /// |  Serialized delimited RpcRequestHeaderProto                         |
    /// +---------------------------------------------------------------------+
    /// |  Serialized delimited IpcConnectionContextProto                     |
    /// +---------------------------------------------------------------------+
    pub fn open(&mut self) {
        self.stream.write(b"hrpc").unwrap();
        let mut version = vec![];
        version.write_u8(9).unwrap();
        self.stream.write(&version[..]).unwrap();
        let mut service_class = vec![];
        service_class.write_u8(0).unwrap();
        self.stream.write(&service_class[..]).unwrap();
        let mut auth = vec![];
        auth.write_u8(0).unwrap();
        self.stream.write(&auth[..]).unwrap();
    }


    /// Sends a Hadoop RPC request to the NameNode.
    ///
    /// +---------------------------------------------------------------------+
    /// |  Length of the next three parts (4 bytes/32 bit int)                |
    /// +---------------------------------------------------------------------+
    /// |  Delimited serialized RpcRequestHeaderProto (varint len + header)   |
    /// +---------------------------------------------------------------------+
    /// |  Delimited serialized RequestHeaderProto (varint len + header)      |
    /// +---------------------------------------------------------------------+
    /// |  Delimited serialized Request (varint len + request)                |
    /// +---------------------------------------------------------------------+
    pub fn send(&self) {
        println!("send");
    }
}
