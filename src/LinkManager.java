import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LinkManager {
    private Map<String, Link> links;
    private static final String FILE_NAME = "links.txt";

    public LinkManager() {
        links = loadLinksFromFile();
    }

    // Загрузка данных из файла
    public Map<String, Link> loadLinksFromFile() {
        File file = new File(FILE_NAME);

        try {
            if (!file.exists()) {
                // Если файл не существует, создаем новый
                file.createNewFile();
            }
        } catch (IOException e) {
                e.printStackTrace();
        }

        Map<String, Link> links = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 5) {
                    String originalUrl = data[0];
                    String shortUrl = data[1];
                    String userUuid = data[2];
                    int visitLimit = Integer.parseInt(data[3]);
                    long expirationTimeMillis = Long.parseLong(data[4]);

                    Link link = new Link(originalUrl, shortUrl, userUuid, visitLimit, expirationTimeMillis);
                    links.put(shortUrl, link);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return links;
    }

    // Сохранение данных в файл
    public void saveLinksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Link link : links.values()) {
                writer.write(link.getOriginalUrl() + ";" + link.getShortUrl() + ";" + link.getUserUuid() + ";" + link.getVisitLimit() + ";" + link.getExpirationTime());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Добавление новой ссылки
    public void addLink(Link link) {
        links.put(link.getShortUrl(), link);
        saveLinksToFile();
    }

    // Получение ссылки по короткому URL
    public Link getLinkByShortUrl(String shortUrl) {
        return links.get(shortUrl);
    }

    // Проверка и обновление количества переходов
    public void checkAndUpdateLink(String shortUrl) {
        Link link = getLinkByShortUrl(shortUrl);
        if (link != null && link.isExpired()) {
            cleanExpiredLinks();
        }
        if (link != null && !link.isExpired() && !link.isLimitReached()) {
            link.decrementVisitLimit();

            if (link.getVisitLimit() <= 0) {
                links.remove(shortUrl);  // Удаляем ссылку из коллекции
                saveLinksToFile();  // Сохраняем изменения в файле
            } else {
                saveLinksToFile();  // Если лимит не исчерпан, просто сохраняем изменения
            }
        }
    }

    // Создание короткой ссылки
    public String createShortLink(String originalStr, String userUuid, int clickLimit, int expiryHours) {
        if (!originalStr.startsWith("http://") && !originalStr.startsWith("https://")) {
            originalStr = "https://" + originalStr;
        }
        String shortUrl = generateShortUrl();
        long expirationTimeMillis = System.currentTimeMillis() + (expiryHours * 60 * 60 * 1000L);
        Link link = new Link(originalStr, shortUrl, userUuid, clickLimit, expirationTimeMillis);
        addLink(link);
        return shortUrl;
    }

    // Получение оригинального URL по короткой ссылке
    public String getOriginalUrl(String shortUrl) {
        Link link = links.get(shortUrl);
        if (link == null || link.isExpired() || link.isLimitReached()) {
            return null;
        }
        return link.getOriginalUrl();
    }

    // Очистка устаревших ссылок
    public void cleanExpiredLinks() {
        List<String> expiredLinks = links.values().stream()
                .filter(Link::isExpired)
                .map(Link::getShortUrl)
                .collect(Collectors.toList());

        for (String shortUrl : expiredLinks) {
            links.remove(shortUrl);
        }
        saveLinksToFile();
    }

    // Изменения лимита
    public void changeLinkLimit(String userUuid, String shortUrl, int limit) {
        // Ищем ссылку по userUuid и shortUrl
        Link currentLink = links.values().stream()
                .filter(link -> link.getUserUuid().equals(userUuid) && link.getShortUrl().equals(shortUrl))
                .findFirst()
                .orElse(null);

        // Если ссылка найдена, меняем лимит
        if (currentLink != null) {
            currentLink.changeLimit(limit);
            saveLinksToFile();
        } else {
            System.out.println("Ссылка не найдена или у вас нет доступа к этой ссылке.");
        }
    }

    // Удаление ссылки
    public void deleteLink(String userUuid, String shortUrl) {
        // Ищем ссылку по userUuid и shortUrl
        Link currentLink = links.values().stream()
                .filter(link -> link.getUserUuid().equals(userUuid) && link.getShortUrl().equals(shortUrl))
                .findFirst()
                .orElse(null);

        // Если ссылка найдена, меняем лимит
        if (currentLink != null) {
            links.remove(currentLink.getShortUrl());
            saveLinksToFile();
        } else {
            System.out.println("Ссылка не найдена или у вас нет доступа к этой ссылке.");
        }
    }

    // Генерация короткого URL
    private String generateShortUrl() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}