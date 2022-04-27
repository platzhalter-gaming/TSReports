package org.mpdev.projects.tsreports.managers;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.mpdev.projects.tsreports.TSReports;
import org.mpdev.projects.tsreports.objects.PlayerLocale;
import org.mpdev.projects.tsreports.utils.FileUtils;
import org.mpdev.projects.tsreports.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigManager {

    private final TSReports plugin;
    private Configuration config;
    private final File configFile;
    private final Map<Locale, Configuration> locales = new HashMap<>();
    private java.util.Locale defaultLocale;

    public ConfigManager(TSReports plugin) {
        this.plugin = plugin;

        Path dataFolder = plugin.getDataFolder().toPath();
        this.configFile = new File(dataFolder + File.separator + "config.yml");

        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setup() throws IOException {
        copyFileFromResources(configFile);
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        // Copies all locale files.
        copyLocalesFromFolder();

        for (Map.Entry<Locale, Object> entry : getLocales().entrySet()) {
            locales.put(entry.getKey(), (Configuration) entry.getValue());
        }
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfig() {
        return config;
    }

    public Map<Locale, Object> getLocales() {
        Map<Locale, Object> locales = new HashMap<>();
        try {
            for (File file : getLocaleFiles()) {
                Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);
                locales.put(locale, ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
            }
            plugin.getLogger().info("Found " + locales.size() + " language files.");
            return locales;
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getMessage(String path, boolean prefix) {
        if (locales.containsKey(defaultLocale)) {
            String msg = locales.get(defaultLocale).getString(path);
            if (msg != null && msg.length() != 0) {
                if (prefix) {
                    String pr = locales.get(defaultLocale).getString("prefix");
                    return Utils.color(msg.replace("%prefix%", pr));
                }
                return Utils.color(msg);
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return "";
    }

    public String getMessage(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        String message;
        String prefix = "";
        if (locales.containsKey(locale)) {
            message = locales.get(locale).getString(path);
            if (message == null || message.isEmpty()) {
                message = locales.get(defaultLocale).getString(path);
            }
            prefix = locales.get(locale).getString("prefix", locales.get(defaultLocale).getString("prefix"));
        } else {
            message = getMessage(path, false);
        }
        return Utils.color(message.replace("%prefix%", prefix));
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyFileFromResources(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists() && !file.isDirectory()) {
            try {
                InputStream inputStream = TSReports.getInstance().getResourceStream(file.toString());
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyLocalesFromFolder(){
        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            return path.endsWith(".yml");
        };
        FileUtils.getFilesIn("locales", filter).forEach(file -> {
            File destination = new File(plugin.getDataFolder().toPath() + File.separator + "locales" + File.separator + file.getName());
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }
            if (!destination.exists() && !destination.isDirectory()) {
                try {
                    Path destinationPath = destination.toPath();
                    if (destinationPath.startsWith(".")) {
                        destinationPath = destinationPath.subpath(1, (int) destination.length());
                    }
                    InputStream inputStream = TSReports.getInstance().getResourceStream(file.toString().replace("\\", "/"));
                    TSReports.getInstance().debug("File copy operation. \nInputStream: " + inputStream + "\nDestination Path: " + destinationPath);
                    Files.copy(inputStream, destinationPath);

                } catch (IOException e) {
                    plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
