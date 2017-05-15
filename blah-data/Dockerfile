FROM blah/rust

RUN apk add --update openssl-dev python g++ make

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-data"]

EXPOSE 8080
