FROM blah/rust

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-count"]

EXPOSE 8080
