use super::super::proto::hdfs::HdfsFileStatusProto;

pub struct File {
    pub src: String,
    pub fs: HdfsFileStatusProto,
}
