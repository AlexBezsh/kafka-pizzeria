INSERT INTO orders(address, phone_number, status, created, updated)
VALUES('Some address', '1234567890', 'COOKING', '2022-02-22T10:15:30.00Z', '2022-02-22T10:15:30.00Z');

INSERT INTO order_items (order_id, name, price, quantity)
VALUES(1, 'Pizza 1', 12.23, 1),
(1, 'Pizza 2', 33.33, 2);