use std::io::prelude::*;
use std::io::{self, BufReader, BufWriter};
use std::net::{TcpStream};
use byteorder::{BigEndian, WriteBytesExt, ReadBytesExt};
use protobuf::{Message, MessageStatic, parse_from_bytes};
use protobuf::error::{ProtobufResult, ProtobufError};
use protobuf::stream::{WithCodedOutputStream, CodedInputStream};
use uuid::Uuid;

use super::super::proto::ProtobufRpcEngine::RequestHeaderProto;
use super::super::proto::RpcHeader::{
    RpcRequestHeaderProto,
    RpcKindProto,
    RpcRequestHeaderProto_OperationProto,
    RpcResponseHeaderProto,
    RpcResponseHeaderProto_RpcStatusProto,
};
use super::super::proto::IpcConnectionContext::{
    IpcConnectionContextProto,
    UserInformationProto,
};

pub struct ChannelFactory;

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

pub struct Request<'a> {
    pub method: &'a str,
    pub message: &'a Message,
}

pub struct Channel {
    reader: BufReader<TcpStream>,
    writer: BufWriter<TcpStream>,
    call_id: i32,
    handshake_sent: bool,
}

impl Channel {
    pub fn send<M : Message + MessageStatic>(&mut self, req: &Request)
            -> ProtobufResult<M> {
        if false == self.handshake_sent {
            try!(self.send_handshake());
            self.handshake_sent = true;
        }

        try!(self.send_message(req));
        self.read_response()
    }

    /// +-----------------------------------------------------------+
    /// |  Length of the RPC response (4 bytes/32 bit int)           |
    /// +-----------------------------------------------------------+
    /// |  Delimited serialized RpcResponseHeaderProto              |
    /// +-----------------------------------------------------------+
    /// |  Serialized delimited RPC response                        |
    /// +-----------------------------------------------------------+
    fn read_response<M : Message + MessageStatic>(&mut self)
            -> ProtobufResult<M> {
        let mut i = CodedInputStream::from_buffered_reader(&mut self.reader);

        let mut size: u32 = 0;
        size += (try!(i.read_raw_byte()) as u32) << 24;
        size += (try!(i.read_raw_byte()) as u32) << 16;
        size += (try!(i.read_raw_byte()) as u32) <<  8;
        size += (try!(i.read_raw_byte()) as u32) <<  0;

        let h = try!(i.read_message::<RpcResponseHeaderProto>());

        if h.get_status() != RpcResponseHeaderProto_RpcStatusProto::SUCCESS {
            return Err(ProtobufError::IoError(io::Error::new(
                io::ErrorKind::Other, "Namenode error")));
        }

        i.read_message()
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
    fn send_message(&mut self, req: &Request) -> ProtobufResult<()> {
        let rpc_header = self.create_rpc_request_header();

        let mut req_header = RequestHeaderProto::new();
        req_header.set_methodName(req.method.to_string());
        req_header.set_declaringClassProtocolName(
            "org.apache.hadoop.hdfs.protocol.ClientProtocol".to_string());
        req_header.set_clientProtocolVersion(1);

        let mut buf = Vec::new();
        try!(self.write_delimited_to_writer(&rpc_header, &mut buf));
        try!(self.write_delimited_to_writer(&req_header, &mut buf));
        try!(self.write_delimited_to_writer(req.message, &mut buf));

        try!(self.writer.write_u32::<BigEndian>(buf.len() as u32)
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.write_all(&buf.as_slice())
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.flush()
                .or_else(|e| Err(ProtobufError::IoError(e))));
        Ok(())
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
    fn send_handshake(&mut self) -> ProtobufResult<()> {
        try!(self.writer.write_all(b"hrpc")
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.write_u8(9)
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.write_u8(0)
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.write_u8(0)
                .or_else(|e| Err(ProtobufError::IoError(e))));

        let rpc_header = self.create_rpc_request_header();
        let mut user = UserInformationProto::new();
        user.set_effectiveUser("dennis".to_string());
        let mut context = IpcConnectionContextProto::new();
        context.set_userInfo(user);
        context.set_protocol(
            "org.apache.hadoop.hdfs.protocol.ClientProtocol".to_string());

        let mut buf = Vec::new();
        self.write_delimited_to_writer(&rpc_header, &mut buf).unwrap();
        self.write_delimited_to_writer(&context, &mut buf).unwrap();
        try!(self.writer.write_u32::<BigEndian>(buf.len() as u32)
                .or_else(|e| Err(ProtobufError::IoError(e))));
        try!(self.writer.write(&buf.as_slice())
                .or_else(|e| Err(ProtobufError::IoError(e))));
        Ok(())
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

    fn write_delimited_to_writer(&self, m: &Message, w: &mut Write)
            -> ProtobufResult<()> {
        w.with_coded_output_stream(|os| {
            let size = m.write_to_bytes().unwrap().len();
            try!(os.write_raw_varint32(size as u32));
            m.write_to(os)
        })
    }
}
