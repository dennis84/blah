FROM blah/rust

RUN apk add --update openssl-dev python g++ make perl

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-collection-processing"]
