FROM ubuntu:14.04

RUN apt-get update
RUN apt-get install -y curl build-essential

RUN curl -sSf https://static.rust-lang.org/rustup.sh | sh -s -- --disable-sudo --channel=nightly

RUN apt-get install -y libssl-dev
