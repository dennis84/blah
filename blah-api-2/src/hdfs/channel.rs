use std::io::prelude::*;
use std::net::TcpStream;
use byteorder::{BigEndian, LittleEndian, WriteBytesExt};
use protobuf::Message;
use protobuf::rt::compute_raw_varint32_size;

use super::super::proto::RpcHeader::RpcRequestHeaderProto;
use super::super::proto::RpcHeader::RpcKindProto;
use super::super::proto::RpcHeader::RpcRequestHeaderProto_OperationProto;
use super::super::proto::IpcConnectionContext::IpcConnectionContextProto;
use super::super::proto::IpcConnectionContext::UserInformationProto;

pub struct ChannelFactory;

impl ChannelFactory {
    pub fn new(addr: &str) -> Result<Channel, String> {
        let stream = TcpStream::connect(addr).unwrap();
        Ok(Channel {
            stream: stream,
            call_id: -3,
        })
    }
}

pub struct Channel {
    stream: TcpStream,
    call_id: i32,
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
        // 68 72 70 63 09 00 0
        self.stream.write_all(b"hrpc").unwrap();
        self.stream.write_u8(9).unwrap();
        self.stream.write_u8(0).unwrap();
        self.stream.write_u8(0).unwrap();
        self.stream.flush();

        let rpc_header = self.create_rpc_request_header();
        let context = self.create_connection_context();

        let rpc_header_bytes = rpc_header.write_to_bytes().unwrap();
        let rpc_header_len = rpc_header_bytes.len() as u32;
        let rpc_header_varint = compute_raw_varint32_size(rpc_header_len);

        let context_bytes = context.write_to_bytes().unwrap();
        let context_len = context_bytes.len() as u32;
        let context_varint = compute_raw_varint32_size(context_len);

        // 00 00 00 56
        let size = rpc_header_len + rpc_header_varint +
                   context_len + context_varint;
        self.stream.write_u32::<BigEndian>(size);

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

    fn create_rpc_request_header(&mut self) -> RpcRequestHeaderProto {
        let mut header = RpcRequestHeaderProto::new();
        header.set_rpcKind(RpcKindProto::RPC_PROTOCOL_BUFFER);
        header.set_rpcOp(
            RpcRequestHeaderProto_OperationProto::RPC_FINAL_PACKET);
        header.set_callId(-3);
        let clientId = String::from("9f9120b1-baed-49");
        let bytes = clientId.into_bytes();
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
}
