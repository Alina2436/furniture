--liquibase formatted sql

--changeset lina:1

insert into items(id, name, description, price, brand, count, img)
values (43, 'Modern White Cabinets',
        'Стильные белые шкафы с минималистичным дизайном идеально подходят для современной кухни', 29490, 'Oakway', 112,
        '/img/kitchen/img_01.png'),
       (44, 'Warm Oak Wall Units', 'Светлые дубовые навесные шкафы создают уют и ощущение натуральности в интерьере.',
        31900, 'Linden & Co.', 76, '/img/kitchen/img_02.png'),
       (45, 'Classic White Shaker',
        'Традиционные белые шкафы с лаконичным дизайном, идеально сочетающиеся с нейтральной плиткой.', 28450,
        'Strathmore', 89, '/img/kitchen/img_03.png'),
       (46, 'Dark Walnut Elegance', 'Глубокие темные ореховые шкафы придают кухне благородный и солидный вид.', 37200,
        'Vanton', 64, '/img/kitchen/img_04.png'),
       (47, 'Sage Green Harmony', 'Светло-зеленые шкафы с мягким оттенком создают атмосферу свежести и спокойствия.',
        30120, 'Oakway', 105, '/img/kitchen/img_05.png'),
       (48, 'Elegant White Glass Cabinets',
        'Белые шкафы с застекленными дверцами придают кухне легкость и воздушность.', 32900, 'Linden & Co.', 58,
        '/img/kitchen/img_06.png'),
       (49, 'Minimalist White Compact',
        'Простые белые шкафы без лишних деталей для лаконичного кухонного пространства.', 26500, 'Strathmore', 92,
        '/img/kitchen/img_07.png'),
       (50, 'Forest Green Classic',
        'Темно-зеленые шкафы с золотыми ручками добавляют кухне роскошный и стильный акцент.', 34150, 'Vanton', 49,
        '/img/kitchen/img_08.png'),
       (51, 'Neutral Grey Simplicity', 'Серые шкафы с мягким оттенком создают ощущение уюта и спокойствия на кухне.',
        31200, 'Oakway', 73, '/img/kitchen/img_09.png');

insert into item_rating(item_id, count, rating)
values (43, 112, 3.89),
       (44, 445, 3.20),
       (45, 358, 3.92),
       (46, 280, 3.67),
       (47, 116, 3.29),
       (48, 81, 4.30),
       (49, 198, 3.11),
       (50, 30, 4.44),
       (51, 112, 4.88);
