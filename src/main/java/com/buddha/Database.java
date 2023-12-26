package com.buddha;

import com.j256.ormlite.dao.*;
import com.j256.ormlite.field.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.*;

import java.sql.SQLException;
import java.util.*;

import static com.buddha.Main.*;

public class Database {
    public static Dao<UserData, Long> userDao;

    @SneakyThrows(Exception.class)
    public static void load() {
        try (var source = new JdbcConnectionSource("jdbc:sqlite:users.db")) {
            userDao = DaoManager.createDao(source, UserData.class);
            TableUtils.createTableIfNotExists(source, UserData.class);
        }
    }

    @SneakyThrows(SQLException.class)
    public static UserData getUserData(long id) {
        return userDao.idExists(id) ? userDao.queryForId(id) : saveUserData(new UserData(id));
    }

    @SneakyThrows(SQLException.class)
    public static UserData saveUserData(UserData data) {
        userDao.createOrUpdate(data);
        return data;
    }

    @NoArgsConstructor(force = true)
    @RequiredArgsConstructor
    public static class UserData {
        @DatabaseField(id = true)
        public final long id;

        @DatabaseField
        public int rolls = 10;

        @DatabaseField
        public int rollsWithoutEpic;

        @DatabaseField
        public int rollsWithoutLegendary;

        @DatabaseField(dataType = DataType.SERIALIZABLE)
        public EnumMap<Drop, Long> drops = new EnumMap<>(Drop.class);

        @AllArgsConstructor
        public enum Drop {
            rogatka(Rarity.rare, "Рогатка", "Это рогатка, но на самом деле это лук."),
            dubina(Rarity.rare, "Дубина Переговоров", "Сигна Дилюка."),
            palka(Rarity.rare, "Палка", "Просто палка. Тебя заскамили."),

            favoniy(Rarity.epic, "Фавоний", "Бесполезная палка."),
            bennet(Rarity.epic, "Беннет", "Ну... Ему не повезло."),
            sianka(Rarity.epic, "Сян Лин", "Отдааааааааааааааааай, мне она не падает("),

            chicha(Rarity.legendary, "Чича", "Замороженный ребенок. Точно не из моего холодильника."),
            diluc(Rarity.legendary, "Дилюк", "Не самый сильный перс."),
            ayaka(Rarity.legendary, "Аяка", "Лучшая вайфочка."),
            hutava(Rarity.legendary, "Ху Тао", "Она тебя закопает.");

            public final Rarity rarity;
            public final String name;
            public final String description;

            public static Drop getRandomDrop(Rarity rarity) {
                var drops = Arrays.stream(values())
                        .filter(drop -> drop.rarity == rarity)
                        .toList();

                return drops.get(random.nextInt(drops.size()));
            }

            @AllArgsConstructor
            public enum Rarity {
                rare(1f, "Редкий", "\uD83D\uDD35", "https://media1.tenor.com/m/KGwWGVz9-XQAAAAC/genshin-impact-wish.gif"),
                epic(0.1f, "Эпический", "\uD83D\uDFE3", "https://media1.tenor.com/m/JcMSVVkgfgMAAAAC/genshin-wish.gif"),
                legendary(0.01f, "Легендарный", "\uD83D\uDFE1", "https://media1.tenor.com/m/YQCvYWzR28wAAAAC/wishing.gif");

                public final float chance;
                public final String name;
                public final String emoji;
                public final String animation;

                public static Rarity getRarity(float chance) {
                    var rarities = Arrays.stream(values())
                            .sorted(Collections.reverseOrder())
                            .toList();

                    // Проверяем шанс каждой редкости
                    for (var rarity : rarities)
                        if (rarity.chance >= chance)
                            return rarity;

                    // Если ни один не подошел, выбираем самую первую редкость
                    return values()[0];
                }
            }
        }
    }
}
