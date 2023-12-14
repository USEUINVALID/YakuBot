package com.buddha;

import com.j256.ormlite.dao.*;
import com.j256.ormlite.field.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.*;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;
import java.util.*;

public class Database {
    public static Dao<UserData, Long> userDao;

    @SneakyThrows(Exception.class)
    public static void load() {
        try (var source = new JdbcConnectionSource("jdbc:sqlite:users.db")){
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

        @DatabaseField(dataType = DataType.SERIALIZABLE)
        public EnumSet<Drop> drops = EnumSet.noneOf(Drop.class);

        @AllArgsConstructor
        public enum Drop {
            rogatka("Рогатка", "Непонятная хуйня, которую ставят на Еимию."),
            dubina("Дубина Переговоров", "Сигна Дилюка."),
            palka("Палка", "Просто палка. Тебя заскамили."),

            favoniy("Фавоний", "Легендарная хуйня."),
            bennet("Беннет", "Ебучий анлак."),
            sianka("Сян Лин", "Сука, мне не падает С6 Сянка, отдай блять."),

            chicha("Чича", "Замороженный ребенок. Точно не из моего холодильника."),
            diluc("Дилюк", "Перс говна."),
            ayaka("Аяка", "Лучшая вайфочка."),
            hutava("Хутава", "Похоронит тебя нахуй.");

            public final String name;
            public final String description;

            public static final List<Drop> rareDrops = List.of(
                    rogatka,
                    dubina,
                    palka
            );

            public static final List<Drop> epicDrops = List.of(
                    favoniy,
                    bennet,
                    sianka
            );

            public static final List<Drop> legendaryDrops = List.of(
                    chicha,
                    diluc,
                    ayaka,
                    hutava
            );

            public static final Map<String, List<Drop>> dropTypeNames = Map.of(
                    "Редкий", rareDrops,
                    "Эпический", epicDrops,
                    "Легендарный", legendaryDrops
            );

            public static final float legendaryDropChance = 0.1f;
            public static final float epicDropChance = 0.2f;
        }
    }
}
