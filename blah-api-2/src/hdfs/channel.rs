use std::io::prelude::*;
use std::io::{self, BufReader, BufWriter};
use std::net::{TcpStream};
use byteorder::{BigEndian, WriteBytesExt, ReadBytesExt};
use protobuf::{Message, MessageStatic};
use protobuf::error::{ProtobufResult, ProtobufError};
use protobuf::stream::{WithCodedOutputStream, CodedInputStream};
use uuid::Uuid;

use super::super::proto::RpcHeader::RpcRequestHeaderProto;
use super::super::proto::RpcHeader::RpcKindProto;
use super::super::proto::RpcHeader::RpcRequestHeaderProto_OperationProto;
use super::super::proto::RpcHeader::RpcResponseHeaderProto;
use super::super::proto::IpcConnectionContext::IpcConnectionContextProto;
use super::super::proto::IpcConnectionContext::UserInformationProto;
use super::super::proto::ProtobufRpcEngine::RequestHeaderProto;

pub struct ChannelFactory;

pub struct Request<'a> {
    pub method: &'a str,
    pub message: &'a Message,
}

impl ChannelFactory {
    pub fn new(addr: &str) -> io::Result<Channel> {
        let stream = TcpStream::connect(addr).unwrap();
        let writer = try!(stream.try_clone());
        Ok(Channel {
            reader: BufReader::new(stream),
            writer: BufWriter::new(writer),
            call_id: -3,
            handshake_sent: false,
        })
    }
}

pub struct Channel {
    reader: BufReader<TcpStream>,
    writer: BufWriter<TcpStream>,
    call_id: i32,
    handshake_sent: bool,
}

impl Channel {
    pub fn send(&mut self, req: &Request) -> ProtobufResult<()> {
        if false == self.handshake_sent {
            self.send_handshake();
        }

        self.send_message(req);
        try!(self.writer.flush().or_else(|e| Err(ProtobufError::IoError(e))));
        Ok(())
    }

    /// +-----------------------------------------------------------+
    /// |  Length of the RPC resonse (4 bytes/32 bit int)           |
    /// +-----------------------------------------------------------+
    /// |  Delimited serialized RpcResponseHeaderProto              |
    /// +-----------------------------------------------------------+
    /// |  Serialized delimited RPC response                        |
    /// +-----------------------------------------------------------+
    pub fn recv<M : Message + MessageStatic>(&mut self) -> ProtobufResult<M> {
        let mut input = CodedInputStream::new(&mut self.reader);
        let len_bytes = input.read_raw_bytes(4).unwrap();

        let mut reader = ::std::io::Cursor::new(len_bytes);
        let total_length = reader.read_u32::<BigEndian>().unwrap();

        let packet = input.read_raw_bytes(total_length).unwrap();
        let mut packet = CodedInputStream::from_bytes(packet.as_slice());

        try!(packet.read_message::<RpcResponseHeaderProto>());
        packet.read_message::<M>()
    }

    /// Sends a Hadoop RPC request to the NameNode.
    ///
    /// +-------------------------------------------------------------------+
    /// |  Length of the next three parts (4 bytes/32 bit int)              |
    /// +-------------------------------------------------------------------+
    /// |  Delimited serialized RpcRequestHeaderProto (varint len + header) |
    /// +-------------------------------------------------------------------+
    /// |  Delimited serialized RequestHeaderProto (varint len + header)    |
    /// +-------------------------------------------------------------------+
    /// |  Delimited serialized Request (varint len + request)              |
    /// +-------------------------------------------------------------------+
    fn send_message(&mut self, req: &Request) {
        let rpc_header = self.create_rpc_request_header();
        let req_header = self.create_request_header(req.method);

        let mut buf = Vec::new();
        self.write_delimited_to_writer(&rpc_header, &mut buf).unwrap();
        self.write_delimited_to_writer(&req_header, &mut buf).unwrap();
        self.write_delimited_to_writer(req.message, &mut buf).unwrap();
        self.writer.write_u32::<BigEndian>(buf.len() as u32).unwrap();
        self.writer.write(&buf.as_slice()).unwrap();
    }

    /// Writes the Hadoop headers.
    ///
    /// +--------------------------------------------------------+
    /// |  Header, 4 bytes ("hrpc")                              |
    /// +--------------------------------------------------------+
    /// |  Version, 1 byte (default verion 9)                    |
    /// +--------------------------------------------------------+
    /// |  RPC service class, 1 byte (0x00)                      |
    /// +--------------------------------------------------------+
    /// |  Auth protocol, 1 byte (Auth method None = 0)          |
    /// +--------------------------------------------------------+
    /// |  Length of the RpcRequestHeaderProto  + length of the  |
    /// |  of the IpcConnectionContextProto (4 bytes/32 bit int) |
    /// +--------------------------------------------------------+
    /// |  Serialized delimited RpcRequestHeaderProto            |
    /// +--------------------------------------------------------+
    /// |  Serialized delimited IpcConnectionContextProto        |
    /// +--------------------------------------------------------+
    fn send_handshake(&mut self) {
        self.writer.write_all(b"hrpc").unwrap();
        self.writer.write_u8(9).unwrap();
        self.writer.write_u8(0).unwrap();
        self.writer.write_u8(0).unwrap();

        let rpc_header = self.create_rpc_request_header();
        let context = self.create_connection_context();

        let mut buf = Vec::new();
        self.write_delimited_to_writer(&rpc_header, &mut buf).unwrap();
        self.write_delimited_to_writer(&context, &mut buf).unwrap();
        self.writer.write_u32::<BigEndian>(buf.len() as u32).unwrap();
        self.writer.write(&buf.as_slice()).unwrap();
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
        let client_id = Uuid::new_v4().simple().to_string();
        header.set_clientId(client_id[0..16].to_string().into_bytes());
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
