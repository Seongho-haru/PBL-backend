FROM eclipse-temurin:17-jdk

ARG JDT_TAR_URL=https://download.eclipse.org/jdtls/milestones/1.37.0/jdt-language-server-1.37.0-202406271335.tar.gz
ARG JDT_TAR_LOCAL=eclipse.jdt.ls.tar.gz

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y wget \
    && rm -rf /var/lib/apt/lists/*

# Download and extract Eclipse JDT LS
RUN mkdir -p /opt/jdtls \
    && cd /opt/jdtls \
    && wget -O ${JDT_TAR_LOCAL} ${JDT_TAR_URL} \
    && tar -xzf ${JDT_TAR_LOCAL} \
    && rm ${JDT_TAR_LOCAL}

# Create simple workspace directory (like Monaco example)
RUN mkdir -p /workspace

# Create a simple Java file (exactly like Monaco example)
RUN echo "public static void main (String[] args) {\n\
    System.out.println(\"Hello World!\");\n\
}" > /workspace/hello.java

# Create jdtls startup script (exactly like Monaco example)
RUN echo '#!/bin/bash\n\
LAUNCHER=$(find /opt/jdtls/plugins -name "org.eclipse.equinox.launcher_*.jar" | head -n 1)\n\
exec java -Declipse.application=org.eclipse.jdt.ls.core.id1 \\\n\
    -Dosgi.bundles.defaultStartLevel=4 \\\n\
    -Declipse.product=org.eclipse.jdt.ls.core.product \\\n\
    -Dlog.level=ALL \\\n\
    -Xmx1G \\\n\
    --add-modules=ALL-SYSTEM \\\n\
    --add-opens \\\n\
    java.base/java.util=ALL-UNNAMED \\\n\
    --add-opens \\\n\
    java.base/java.lang=ALL-UNNAMED \\\n\
    -jar "$LAUNCHER" \\\n\
    -configuration /opt/jdtls/config_linux \\\n\
    -data /workspace \\\n\
    "$@"' > /usr/local/bin/jdtls \
    && chmod +x /usr/local/bin/jdtls

# Set proper permissions
RUN chmod -R 755 /workspace

# Create working directory
WORKDIR /workspace

# 컨테이너 유지 (LSP는 exec로 시작)
CMD ["tail", "-f", "/dev/null"]
