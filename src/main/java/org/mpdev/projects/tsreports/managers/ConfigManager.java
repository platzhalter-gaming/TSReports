package org.mpdev.projects.tsreports.managers;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.PlayerLocale;
import org.mpdev.projects.tsreports.utils.FileUtils;
import org.mpdev.projects.tsreports.utils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigManager {
    private final TSReports plugin;
    private Configuration config;
    private final Map<Locale, Configuration> locales = new HashMap<>();
    private Map<String, String> embeds;
    private List<String> bannedPlayers = new ArrayList<>();
    private java.util.Locale defaultLocale;

    public ConfigManager(TSReports plugin) {
        this.plugin = plugin;
        setup();
    }

    public Configuration getConfig() {
        return config;
    }

    public Map<Locale, Object> getLocales() {
        Map<Locale, Object> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);

            try {
                locales.put(locale, ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Found " + locales.size() + " language files.");
        return locales;
    }

    public List<Locale> getAvailableLocales() {
        List<Locale> locales = new ArrayList<>();
        for (Map.Entry<Locale, Configuration> locale : this.locales.entrySet()) {
            locales.add(locale.getKey());
        }
        return locales;
    }

    public List<File> getLocaleFiles() {
        List<File> files = new ArrayList<>();
        File directoryPath = new File(plugin.getDataFolder() + File.separator + "locales");
        FilenameFilter ymlFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".yml");
        };
        File[] filesList = directoryPath.listFiles(ymlFilter);
        Objects.requireNonNull(filesList, "Locales folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    public String getMessage(String path) {
        if (locales.containsKey(defaultLocale)) {
            String msg = locales.get(defaultLocale).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(msg);
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return path;
    }

    public String getMessage(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        String message;
        String prefix = "";
        if (locales.containsKey(locale)) {
            message = locales.get(locale).getString(path);
            if (message == null || message.isEmpty()) {
                message = locales.get(defaultLocale).getString(path);
                if (message == null || message.isEmpty()) {
                    message = path;
                }
            }
            prefix = locales.get(locale).getString("prefix", locales.get(defaultLocale).getString("prefix"));
        } else {
            message = getMessage(path);
        }
        return Utils.color(message.replace("%prefix%", prefix));
    }

    public Configuration getSection(String path) {
        if (locales.containsKey(defaultLocale)) {
            Configuration config = locales.get(defaultLocale).getSection(path);
            if (config != null) {
                return config;
            }
        }
        plugin.getLogger().warning("The searched section was not found in the language file and the default language file: " + path);
        return null;
    }

    public Configuration getSection(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        Configuration configuration;
        if (locales.containsKey(locale)) {
            configuration = locales.get(locale).getSection(path);
            if (configuration == null) {
                configuration = locales.get(defaultLocale).getSection(path);
                if (configuration == null) {
                    plugin.getLogger().warning("The searched section was not found in the language file and the default language file: " + path);
                    return null;
                }
            }
        } else {
            configuration = getSection(path);
        }
        return configuration;
    }

    public String getMessage(String path, String playerName, Function<String, String> placeholders) {
        String message = getMessage(path, playerName);
        return Utils.color(placeholders.apply(message));
    }

    public int getInteger(String path) {
        if (locales.containsKey(defaultLocale)) {
            return locales.get(defaultLocale).getInt(path);
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return 0;
    }

    public int getInteger(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        if (locales.containsKey(locale)) {
            return locales.get(locale).getInt(path);
        } else {
            return getInteger(path);
        }
    }

    public List<String> getStringList(String path) {
        if (locales.containsKey(defaultLocale)) {
            List<String> stringList = locales.get(defaultLocale).getStringList(path);
            if (!stringList.isEmpty()) {
                return stringList.stream().map(Utils::color).collect(Collectors.toList());
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return new ArrayList<>();
    }

    public List<String> getStringList(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        List<String> stringList;
        if (locales.containsKey(locale)) {
            stringList = locales.get(locale).getStringList(path);
        } else {
            stringList = getStringList(path);
        }
        return stringList.stream().map(Utils::color).collect(Collectors.toList());
    }

    public Set<File> getEmbedFiles() {
        Set<File> files = new HashSet<>();
        File directoryPath = new File(plugin.getDataFolder(), "embeds");
        FilenameFilter jsonFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".json");
        };
        File[] filesList = directoryPath.listFiles(jsonFilter);
        Objects.requireNonNull(filesList, "Embeds folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    public Map<String, String> getEmbeds() {
        Map<String, String> embeds = new HashMap<>();
        for (File file : getEmbedFiles()) {
            try {
                embeds.put(file.getName().split("\\.")[0].toUpperCase(java.util.Locale.ENGLISH), fileToString(file, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Found " + embeds.size() + " embed files.");
        return embeds;
    }

    public String fileToString(File file, String charset) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString(charset);
    }

    public String getEmbed(String path) {
        return embeds.get(path);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public List<String> getBannedPlayers() {
        return bannedPlayers;
    }

    public void setup() {
        this.config = copyConfigFile();
        // Copies all locale files.
        copyFilesFromFolder("locales");
        // Copies all embed files.
        copyFilesFromFolder("embeds");

        for (Map.Entry<Locale, Object> entry : getLocales().entrySet()) {
            locales.put(entry.getKey(), (Configuration) entry.getValue());
        }
        this.embeds = getEmbeds();
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
        bannedPlayers = getConfig().getStringList("banned-players");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Configuration copyConfigFile() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = plugin.getResourceStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyFilesFromFolder(String folder) {
        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            if (folder.equals("locales")) {
                return path.endsWith(".yml");
            }
            if (folder.equals("embeds")) {
                return path.endsWith(".json");
            }
            return false;
        };
        FileUtils.getFilesIn(folder, filter).forEach(file -> {
            File destination = new File(plugin.getDataFolder(), folder + File.separator + file.getName());
            if (!destination.getParentFile().exists())
                destination.getParentFile().mkdir();
            if (!destination.exists() && !destination.isDirectory()) {
                String fileString = file.toString().replace("\\", "/");
                if (fileString.startsWith("."))
                    fileString.substring(1);
                if (fileString.startsWith("/"))
                    fileString.substring(1);
                try (InputStream in = plugin.getResourceStream(fileString)) {
                    Files.copy(in, destination.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}