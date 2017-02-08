FROM ubuntu:14.04

RUN apt-get update
RUN apt-get install -y curl build-essential libssl-dev pkg-config

RUN curl https://sh.rustup.rs -sSf | sh -s -- -y

ENV PATH "$PATH:/root/.cargo/bin"
