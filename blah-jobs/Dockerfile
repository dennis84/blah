FROM blah/rust

RUN mkdir /app
ADD . /app

WORKDIR /app

RUN cargo build --release

ENTRYPOINT ["target/release/blah-jobs"]

EXPOSE 8080
