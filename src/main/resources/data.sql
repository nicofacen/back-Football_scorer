INSERT INTO clubes (nombre, pais, escudo_url)
VALUES ('Boca Juniors', 'Argentina', 'https://ejemplo.com/boca.png');

INSERT INTO clubes (nombre, pais, escudo_url)
VALUES ('River Plate', 'Argentina', 'https://ejemplo.com/river.png');

INSERT INTO clubes (nombre, pais, escudo_url)
VALUES ('Argentina', 'Argentina', 'https://ejemplo.com/argentina.png');

INSERT INTO clubes (nombre, pais, escudo_url)
VALUES ('Barcelona', 'España', 'https://ejemplo.com/barcelona.png');

INSERT INTO clubes (nombre, pais, escudo_url)
VALUES ('Real Madrid', 'España', 'https://ejemplo.com/real-madrid.png');


INSERT INTO categorias (nombre, descripcion)
VALUES ('Camisetas', 'Camisetas de clubes y selecciones');

INSERT INTO categorias (nombre, descripcion)
VALUES ('Botines', 'Botines de fútbol');

INSERT INTO categorias (nombre, descripcion)
VALUES ('Pelotas', 'Pelotas de fútbol');

INSERT INTO categorias (nombre, descripcion)
VALUES ('Accesorios', 'Accesorios de fútbol');

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Camiseta Boca Titular', 'Camiseta oficial de Boca Juniors', 95000, 10, 'https://ejemplo.com/boca-camiseta.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Boca Juniors'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Camiseta River Titular', 'Camiseta oficial de River Plate', 95000, 10, 'https://ejemplo.com/river-camiseta.png', true,
 (SELECT id FROM clubes WHERE nombre = 'River Plate'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Camiseta Argentina Titular', 'Camiseta de la Selección Argentina', 110000, 15, 'https://ejemplo.com/argentina-camiseta.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Argentina'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Camiseta Barcelona Titular', 'Camiseta oficial del Barcelona', 120000, 8, 'https://ejemplo.com/barcelona-camiseta.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Barcelona'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Camiseta Real Madrid Titular', 'Camiseta oficial del Real Madrid', 120000, 8, 'https://ejemplo.com/real-camiseta.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Real Madrid'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Botines Predator', 'Botines para fútbol', 155000, 7, 'https://ejemplo.com/predator.png', true, NULL);

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Botines Mercurial', 'Botines de velocidad', 170000, 6, 'https://ejemplo.com/mercurial.png', true, NULL);

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Pelota Argentina', 'Pelota inspirada en la Selección Argentina', 48000, 20, 'https://ejemplo.com/pelota-argentina.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Argentina'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Pelota Champions', 'Pelota profesional', 55000, 15, 'https://ejemplo.com/pelota-champions.png', true, NULL);

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Gorra Boca Juniors', 'Gorra oficial', 32000, 12, 'https://ejemplo.com/gorra-boca.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Boca Juniors'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Gorra River Plate', 'Gorra oficial', 32000, 12, 'https://ejemplo.com/gorra-river.png', true,
 (SELECT id FROM clubes WHERE nombre = 'River Plate'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Bufanda Barcelona', 'Bufanda oficial', 28000, 10, 'https://ejemplo.com/bufanda-barcelona.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Barcelona'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Medias Real Madrid', 'Medias deportivas', 22000, 16, 'https://ejemplo.com/medias-real.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Real Madrid'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Short Argentina', 'Short de la Selección Argentina', 58000, 10, 'https://ejemplo.com/short-argentina.png', true,
 (SELECT id FROM clubes WHERE nombre = 'Argentina'));

INSERT INTO productos
(nombre, descripcion, precio, stock, imagen_url, activo, club_id)
VALUES
('Mochila Fútbol', 'Mochila deportiva', 65000, 9, 'https://ejemplo.com/mochila.png', true, NULL);

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre = 'Camiseta Boca Titular'
  AND c.nombre = 'Camisetas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre = 'Camiseta River Titular'
  AND c.nombre = 'Camisetas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre = 'Camiseta Argentina Titular'
  AND c.nombre = 'Camisetas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre = 'Camiseta Barcelona Titular'
  AND c.nombre = 'Camisetas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre = 'Camiseta Real Madrid Titular'
  AND c.nombre = 'Camisetas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre IN ('Botines Predator', 'Botines Mercurial')
  AND c.nombre = 'Botines';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre IN ('Pelota Argentina', 'Pelota Champions')
  AND c.nombre = 'Pelotas';

INSERT INTO productos_categorias (producto_id, categoria_id)
SELECT p.id, c.id
FROM productos p, categorias c
WHERE p.nombre IN (
    'Gorra Boca Juniors',
    'Gorra River Plate',
    'Bufanda Barcelona',
    'Medias Real Madrid',
    'Short Argentina',
    'Mochila Fútbol'
)
AND c.nombre = 'Accesorios';

INSERT INTO usuarios
(nombre, apellido, email, password, rol)
VALUES
(
    'Admin',
    'Demo',
    'admin@demo.com',
    '$2a$10$Jt3.OS571LEdfxxb2I2EUexirHvHJ6ys9Z70sv7KV4OZnMqHmHi/q',
    'ADMIN'
);

INSERT INTO usuarios
(nombre, apellido, email, password, rol)
VALUES
(
    'Cliente',
    'Demo',
    'cliente@demo.com',
    '$2a$10$2P1L86oskdlCjRyh/CKPtOfz079On7YP/I8YHJD.azT1hNA0tPsDu',
    'CLIENTE'
);