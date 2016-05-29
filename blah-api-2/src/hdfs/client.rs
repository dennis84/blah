use protobuf::error::{ProtobufResult};
use super::channel::{Channel, Request};
use super::writer::{Writer};

use super::super::proto::hdfs::FsPermissionProto;
use super::super::proto::ClientNamenodeProtocol::{
    MkdirsRequestProto,
    MkdirsResponseProto,
    DeleteRequestProto,
    DeleteResponseProto,
    CreateRequestProto,
    CreateResponseProto,
    CompleteRequestProto,
    CompleteResponseProto,
    GetFileInfoRequestProto,
    GetFileInfoResponseProto,
    GetServerDefaultsRequestProto,
    GetServerDefaultsResponseProto,
};

pub trait Client {
    fn exists(&mut self, src: &str)
        -> ProtobufResult<GetFileInfoResponseProto>;
    fn mkdir(&mut self, src: &str)
        -> ProtobufResult<MkdirsResponseProto>;
    fn rm(&mut self, src: &str)
        -> ProtobufResult<DeleteResponseProto>;
    fn touch(&mut self, src: &str)
        -> ProtobufResult<CompleteResponseProto>;
    fn get_server_defaults(&mut self)
        -> ProtobufResult<GetServerDefaultsResponseProto>;
    fn create(&mut self, src: &str)
        -> Writer;
}

impl Client for Channel {
    fn exists(&mut self, src: &str)
            -> ProtobufResult<GetFileInfoResponseProto> {
        let mut req = GetFileInfoRequestProto::new();
        req.set_src(src.to_string());
        self.send(&Request {
            method: "getFileInfo",
            message: &req,
        })
    }

    fn mkdir(&mut self, src: &str) -> ProtobufResult<MkdirsResponseProto> {
        let mut perm = FsPermissionProto::new();
        perm.set_perm(777);
        let mut req = MkdirsRequestProto::new();
        req.set_src(src.to_string());
        req.set_masked(perm);
        req.set_createParent(false);
        self.send(&Request {
            method: "mkdirs",
            message: &req,
        })
    }

    fn rm(&mut self, src: &str) -> ProtobufResult<DeleteResponseProto> {
        let mut req = DeleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_recursive(false);
        self.send(&Request {
            method: "delete",
            message: &req,
        })
    }

    fn touch(&mut self, src: &str) -> ProtobufResult<CompleteResponseProto> {
        let defaults = try!(self.get_server_defaults());
        let defaults = defaults.get_serverDefaults();
        let mut perm = FsPermissionProto::new();
        perm.set_perm(644);
        let mut req = CreateRequestProto::new();
        req.set_src(src.to_string());
        req.set_masked(perm);
        req.set_clientName("blah".to_string());
        req.set_createFlag(0x01);
        req.set_createParent(false);
        req.set_replication(defaults.get_replication());
        req.set_blockSize(defaults.get_blockSize());
        try!(self.send::<CreateResponseProto>(&Request {
            method: "create",
            message: &req,
        }));

        let mut req = CompleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_clientName("blah".to_string());
        self.send(&Request {method: "complete", message: &req})
    }

    fn get_server_defaults(&mut self)
            -> ProtobufResult<GetServerDefaultsResponseProto> {
        let req = GetServerDefaultsRequestProto::new();
        self.send(&Request {
            method: "getServerDefaults",
            message: &req,
        })
    }

    fn create(&mut self, src: &str) -> Writer {
        Writer {}
    }
}
