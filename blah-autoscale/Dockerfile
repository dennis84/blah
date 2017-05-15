FROM blah/rust

RUN apk add --update openssl-dev make perl

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-autoscale"]
