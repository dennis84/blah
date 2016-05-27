use protobuf::error::ProtobufResult;
use super::channel::{Channel, Request};
use super::writer::{Writer};

use super::super::proto::hdfs::FsPermissionProto;
use super::super::proto::ClientNamenodeProtocol::{
    MkdirsRequestProto,
    MkdirsResponseProto,
    DeleteRequestProto,
    DeleteResponseProto,
    CreateRequestProto,
    CompleteRequestProto,
    CompleteResponseProto,
    GetFileInfoRequestProto,
    GetFileInfoResponseProto,
};

pub trait Client {
    fn exists(&mut self, src: &str)
        -> ProtobufResult<GetFileInfoResponseProto>;
    fn mkdir(&mut self, src: &str)
        -> ProtobufResult<MkdirsResponseProto>;
    fn rmdir(&mut self, src: &str)
        -> ProtobufResult<DeleteResponseProto>;
    fn touch(&mut self, src: &str)
        -> ProtobufResult<CompleteResponseProto>;
    fn create(&mut self, src: &str)
        -> Writer;
}

impl Client for Channel {
    fn exists(&mut self, src: &str)
            -> ProtobufResult<GetFileInfoResponseProto> {
        let mut req = GetFileInfoRequestProto::new();
        req.set_src(src.to_string());
        try!(self.send(&Request {
            method: "getFileInfo",
            message: &req,
        }));

        self.recv::<GetFileInfoResponseProto>()
    }

    fn mkdir(&mut self, src: &str) -> ProtobufResult<MkdirsResponseProto> {
        let mut perm = FsPermissionProto::new();
        perm.set_perm(777);
        let mut req = MkdirsRequestProto::new();
        req.set_src(src.to_string());
        req.set_masked(perm);
        req.set_createParent(false);
        try!(self.send(&Request {
            method: "mkdirs",
            message: &req,
        }));

        self.recv::<MkdirsResponseProto>()
    }

    fn rmdir(&mut self, src: &str) -> ProtobufResult<DeleteResponseProto> {
        let mut req = DeleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_recursive(false);
        try!(self.send(&Request {
            method: "delete",
            message: &req,
        }));

        self.recv::<DeleteResponseProto>()
    }

    fn touch(&mut self, src: &str) -> ProtobufResult<CompleteResponseProto> {
        let mut perm = FsPermissionProto::new();
        perm.set_perm(644);
        let mut req = CreateRequestProto::new();
        req.set_src(src.to_string());
        req.set_masked(perm);
        req.set_clientName("blah".to_string());
        req.set_createFlag(0x01);
        req.set_createParent(false);
        req.set_replication(1);
        req.set_blockSize(134217728);
        try!(self.send(&Request {method: "create", message: &req}));

        let mut req = CompleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_clientName("blah".to_string());
        try!(self.send(&Request {method: "complete", message: &req}));

        self.recv::<CompleteResponseProto>()
    }

    fn create(&mut self, src: &str) -> Writer {
        Writer {}
    }
}
