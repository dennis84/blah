use super::channel::{Channel, Request};
use super::writer::{Writer};
use super::super::proto::hdfs::FsPermissionProto;
use super::super::proto::ClientNamenodeProtocol::MkdirsRequestProto;
use super::super::proto::ClientNamenodeProtocol::DeleteRequestProto;
use super::super::proto::ClientNamenodeProtocol::CreateRequestProto;
use super::super::proto::ClientNamenodeProtocol::CompleteRequestProto;

pub trait Client {
    fn mkdir(&mut self, src: &str);
    fn rmdir(&mut self, src: &str);
    fn touch(&mut self, src: &str);
    fn create(&mut self, src: &str) -> Writer;
}

impl Client for Channel {
    fn mkdir(&mut self, src: &str) {
        let mut perm = FsPermissionProto::new();
        perm.set_perm(777);
        let mut req = MkdirsRequestProto::new();
        req.set_src(src.to_string());
        req.set_masked(perm);
        req.set_createParent(false);
        self.send(&Request {
            method: "mkdirs",
            message: &req,
        });
    }

    fn rmdir(&mut self, src: &str) {
        let mut req = DeleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_recursive(false);
        self.send(&Request {
            method: "delete",
            message: &req,
        });
    }

    fn touch(&mut self, src: &str) {
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
        self.send(&Request {method: "create", message: &req});

        let mut req = CompleteRequestProto::new();
        req.set_src(src.to_string());
        req.set_clientName("blah".to_string());
        self.send(&Request {method: "complete", message: &req});
    }

    fn create(&mut self, src: &str) -> Writer {
        Writer {}
    }
}
