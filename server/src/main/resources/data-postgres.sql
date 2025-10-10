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

-- Бронирования (PostgreSQL)
INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (1, 1, 2, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 'APPROVED');

INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (2, 1, 3, CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '3 days', 'APPROVED');

INSERT INTO bookings (id, item_id, booker_id, start_time, end_time, status) VALUES
  (3, 2, 3, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 'APPROVED');

-- Комментарии
INSERT INTO comments (id, text, item_id, author_id, created) VALUES
  (1, 'Отличная дрель, всё работает!', 1, 2, CURRENT_TIMESTAMP - INTERVAL '12 hours'),
  (2, 'Инструмент в хорошем состоянии.', 2, 3, CURRENT_TIMESTAMP - INTERVAL '6 hours');