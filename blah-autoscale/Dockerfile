FROM blah/rust

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENV RUST_LOG info

ENTRYPOINT ["target/release/blah-autoscale"]
