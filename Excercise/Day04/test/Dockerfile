# Use Alpine as the base image (small and efficient)
FROM alpine:latest

# Install curl (and bash if you need it for script execution)
RUN apk update && \
    apk add --no-cache curl bash

# Set the working directory to /app
WORKDIR /app

# Copy the test.sh script into the container
COPY test.sh .

# Make sure the script is executable
RUN chmod +x test.sh

# Execute the script (which will use curl inside the script)
CMD ["./test.sh"]