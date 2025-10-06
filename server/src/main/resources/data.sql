-- Очистка
DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM item_requests;
DELETE FROM users;

-- Пользователи
INSERT INTO users (id, name, email) VALUES
(1, 'Иван', 'ivan@mail.ru'),
(2, 'Мария', 'maria@mail.ru'),
(3, 'Петр', 'petr@mail.ru');

-- Запросы
INSERT INTO item_requests (id, description, requester_id, created) VALUES
(1, 'Ищу дрель на выходные', 3, TIMESTAMP '2025-10-03 10:00:00'),
(2, 'Нужна лестница для покраски', 2, TIMESTAMP '2025-10-01 10:00:00');

-- Вещи
INSERT INTO items (id, name, description, available, owner_id, request_id) VALUES
(1, 'Дрель', 'Дрель Bosch, почти новая', TRUE, 1, 1),
(2, 'Лестница', 'Устойчивая алюминиевая лестница', TRUE, 1, 2);

-- Бронирования
INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (1, 1, 2,
     DATEADD('DAY', -5, CURRENT_TIMESTAMP),
     DATEADD('DAY', -4, CURRENT_TIMESTAMP),
     'APPROVED');

INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (2, 1, 3,
     DATEADD('DAY',  2, CURRENT_TIMESTAMP),
     DATEADD('DAY',  3, CURRENT_TIMESTAMP),
     'APPROVED');

INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (3, 2, 3,
     DATEADD('DAY', -2, CURRENT_TIMESTAMP),
     DATEADD('DAY', -1, CURRENT_TIMESTAMP),
     'APPROVED');

-- Комментарии
INSERT INTO comments (id, text, item_id, author_id, created) VALUES
  (1, 'Отличная дрель, всё работает!', 1, 2, DATEADD('HOUR', -12, CURRENT_TIMESTAMP)),
  (2, 'Инструмент в хорошем состоянии.', 2, 3, DATEADD('HOUR', -6, CURRENT_TIMESTAMP));


ALTER TABLE users          ALTER COLUMN id RESTART WITH 100;
ALTER TABLE item_requests  ALTER COLUMN id RESTART WITH 100;
ALTER TABLE items          ALTER COLUMN id RESTART WITH 100;
ALTER TABLE bookings       ALTER COLUMN id RESTART WITH 100;
ALTER TABLE comments       ALTER COLUMN id RESTART WITH 100;