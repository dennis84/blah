use super::channel::{Channel, Request};
use super::super::proto::hdfs::FsPermissionProto;
use super::super::proto::ClientNamenodeProtocol::MkdirsRequestProto;
use super::super::proto::ClientNamenodeProtocol::DeleteRequestProto;

pub trait Client {
    fn mkdir(&mut self, src: &str);
    fn rmdir(&mut self, src: &str);
    fn write(&mut self, src: &str);
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

    fn write(&mut self, src: &str) {
    }
}
