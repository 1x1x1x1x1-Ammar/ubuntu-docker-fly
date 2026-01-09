FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

CMD ["bash", "-lc", "echo 'Hello from Ubuntu Docker on Fly.io 🚀' && uname -a && sleep 3600"]
