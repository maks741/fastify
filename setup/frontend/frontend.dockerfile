# use the node image
FROM node:alpine

WORKDIR /app

# copy the files describing the dependencies into the workdir ('/app')
COPY package.json ./
COPY package-lock.json ./

# install the dependencies including the angular cli
RUN ["npm", "install", "-g", "@angular/cli"]
RUN ["npm", "install"]

# copy the source code to the container
COPY . .

EXPOSE 4200

CMD ["ng", "serve", "--host", "0.0.0.0"]
