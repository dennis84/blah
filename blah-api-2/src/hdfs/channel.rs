use std::io::prelude::*;
use std::net::{TcpStream};
use byteorder::{BigEndian, WriteBytesExt};
use protobuf::{Message};
use protobuf::error::{ProtobufResult};
use protobuf::stream::{WithCodedOutputStream};
use protobuf::reflect::{MessageDescriptor};

use super::super::proto::RpcHeader::RpcRequestHeaderProto;
use super::super::proto::RpcHeader::RpcKindProto;
use super::super::proto::RpcHeader::RpcRequestHeaderProto_OperationProto;
use super::super::proto::IpcConnectionContext::IpcConnectionContextProto;
use super::super::proto::IpcConnectionContext::UserInformationProto;
use super::super::proto::ProtobufRpcEngine::RequestHeaderProto;

pub struct ChannelFactory;

pub struct Request<'a> {
    pub method: &'a str,
    pub message: &'a Message,
}

impl ChannelFactory {
    pub fn new(addr: &str) -> Result<Channel, String> {
        let stream = TcpStream::connect(addr).unwrap();
        Ok(Channel {
            stream: stream,
            call_id: -3,
            handshake_sent: false,
        })
    }
}

pub struct Channel {
    stream: TcpStream,
    call_id: i32,
    handshake_sent: bool,
}

impl Channel {
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
    pub fn send(&mut self, req: &Request) {
        if false == self.handshake_sent {
            self.send_handshake();
        }

        let rpc_header = self.create_rpc_request_header();
        let req_header = self.create_request_header(req.method);

        let mut buf = Vec::new();
        self.write_delimited_to_writer(&rpc_header, &mut buf).unwrap();
        self.write_delimited_to_writer(&req_header, &mut buf).unwrap();
        self.write_delimited_to_writer(req.message, &mut buf).unwrap();
        self.stream.write_u32::<BigEndian>(buf.len() as u32).unwrap();
        self.stream.write(&buf.as_slice()).unwrap();
    }

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
    fn send_handshake(&mut self) {
        self.stream.write_all(b"hrpc").unwrap();
        self.stream.write_u8(9).unwrap();
        self.stream.write_u8(0).unwrap();
        self.stream.write_u8(0).unwrap();

        let rpc_header = self.create_rpc_request_header();
        let context = self.create_connection_context();

        let mut buf = Vec::new();
        self.write_delimited_to_writer(&rpc_header, &mut buf).unwrap();
        self.write_delimited_to_writer(&context, &mut buf).unwrap();
        self.stream.write_u32::<BigEndian>(buf.len() as u32).unwrap();
        self.stream.write(&buf.as_slice()).unwrap();
    }

    fn create_request_header(&self, method: &str) -> RequestHeaderProto {
        let mut header = RequestHeaderProto::new();
        header.set_methodName(method.to_string());
        header.set_declaringClassProtocolName(
            "org.apache.hadoop.hdfs.protocol.ClientProtocol".to_string());
        header.set_clientProtocolVersion(1);
        header
    }

    fn create_rpc_request_header(&mut self) -> RpcRequestHeaderProto {
        let mut header = RpcRequestHeaderProto::new();
        header.set_rpcKind(RpcKindProto::RPC_PROTOCOL_BUFFER);
        header.set_rpcOp(
            RpcRequestHeaderProto_OperationProto::RPC_FINAL_PACKET);
        header.set_callId(self.call_id);
        let client_id = String::from("c683f954-789f-4d");
        let bytes = client_id.into_bytes();
        header.set_clientId(bytes);
        header.set_retryCount(-1);

        if self.call_id == -3 {
            self.call_id = 0
        } else {
            self.call_id += 1
        }

        header
    }

    fn create_connection_context(&mut self) -> IpcConnectionContextProto {
        let mut user = UserInformationProto::new();
        user.set_effectiveUser("dennis".to_string());
        let mut context = IpcConnectionContextProto::new();
        context.set_userInfo(user);
        context.set_protocol(
            "org.apache.hadoop.hdfs.protocol.ClientProtocol".to_string());
        context
    }

    fn write_delimited_to_writer(&self, m: &Message, w: &mut Write)
            -> ProtobufResult<()> {
        w.with_coded_output_stream(|os| {
            let size = m.write_to_bytes().unwrap().len();
            try!(os.write_raw_varint32(size as u32));
            m.write_to(os)
        })
    }
}
