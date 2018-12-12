create database if not exists `web-crew-dev`;

CREATE TABLE  IF NOT EXISTS tb_product_info (
  id VARCHAR(50) PRIMARY  KEY,
  goodsId VARCHAR(100) NOT NULL,
  source VARCHAR(30) ,
  url VARCHAR(200) ,
  title VARCHAR (180),
  imageUrl VARCHAR(280),
  price DOUBLE(8,2),
  commentCnt INT,
  goodRate DOUBLE(5,2),
  params Text
);

