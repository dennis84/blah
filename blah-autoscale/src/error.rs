use std::io;
use std::fmt;
use hyper;
use rustc_serialize::json;

pub type AutoscaleResult<T> = Result<T, Error>;

#[derive(Debug)]
pub enum Error {
    IoError(io::Error),
}

impl fmt::Display for Error {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        fmt::Debug::fmt(self, f)
    }
}

impl From<io::Error> for Error {
    fn from(err: io::Error) -> Error {
        Error::IoError(err)
    }
}

impl From<hyper::Error> for Error {
    fn from(err: hyper::Error) -> Error {
        Error::IoError(io::Error::new(
            io::ErrorKind::Other,
            err.to_string()
        ))
    }
}

impl From<json::ParserError> for Error {
    fn from(err: json::ParserError) -> Error {
        Error::IoError(io::Error::new(
            io::ErrorKind::Other,
            err.to_string()
        ))
    }
}
