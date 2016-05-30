use protobuf::error::{ProtobufResult};
use super::channel::{Channel, Request};
use super::writer::{File};
use std::io::prelude::*;
use std::io::{self, BufWriter};

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
    GetBlockLocationsRequestProto,
    GetBlockLocationsResponseProto,
    AddBlockRequestProto,
    AddBlockResponseProto,
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
    fn cat(&mut self, src: &str)
        -> ProtobufResult<GetBlockLocationsResponseProto>;
    fn create(&mut self, src: &str)
        -> ProtobufResult<File>;
    fn write(&mut self, file: &File, buf: &[u8])
        -> ProtobufResult<()>;                         
    fn close(&mut self, file: &File)
        -> ProtobufResult<()>;
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

    fn cat(&mut self, src: &str)
            -> ProtobufResult<GetBlockLocationsResponseProto> {
        let mut req = GetBlockLocationsRequestProto::new();
        req.set_src(src.to_string());
        req.set_length(0);
        req.set_offset(0);
        self.send(&Request {
            method: "getBlockLocations",
            message: &req,
        })
    }

    fn create(&mut self, src: &str) -> ProtobufResult<File> {
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
        let mut resp: CreateResponseProto = try!(self.send(&Request {
            method: "create",
            message: &req,
        }));

        Ok(File {
            src: src.to_string(),
            fs: resp.take_fs(),
        })
    }

    fn write(&mut self, file: &File, buf: &[u8]) -> ProtobufResult<()> {
        let mut req = AddBlockRequestProto::new();
        req.set_src(file.src.clone());
        req.set_clientName("blah".to_string());
        let mut resp: AddBlockResponseProto = try!(self.send(&Request {
            method: "addBlock",
            message: &req,
        }));

        Ok(())
    }

    fn close(&mut self, file: &File) -> ProtobufResult<()> {
        Ok(())
    }
}
