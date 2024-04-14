package com.andriyklus.dota2.telegram.service;


import com.andriyklus.dota2.domain.*;
import com.andriyklus.dota2.service.db.UkrainianTeamService;
import com.andriyklus.dota2.telegram.messagesender.MessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Service
public class SendMessageService {

    private final Long CHAT_ID = /*358029493L;*/ -1002029412738L;

    private final MessageSender messageSender;
    private final UkrainianTeamService ukrainianTeamService;


    public SendMessageService(MessageSender messageSender, UkrainianTeamService ukrainianTeamService) {
        this.messageSender = messageSender;
        this.ukrainianTeamService = ukrainianTeamService;
    }

    //posting GameInside news

    public void postGameInsideNews(GameinsideNewsPost gameinsideNewsPost) {
        var message = SendPhoto.builder()
                .chatId(CHAT_ID)
                .caption(formatMessageForGameinsideNews(gameinsideNewsPost))
                .photo(new InputFile(gameinsideNewsPost.getImageUrl()))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendPhoto(message);

    }

    private String formatMessageForGameinsideNews(GameinsideNewsPost gameinsideNewsPost) {
        return "<b>" +
                gameinsideNewsPost.getHeader() +
                "</b>" +
                "\n\n" +
                gameinsideNewsPost.getBody() +
                "\n" +
                "<a href=\"" +
                gameinsideNewsPost.getNewsUrl() +
                "\">" +
                "Прочитати повну статтю" +
                "</a>" +
                "\n\n" +
                "#" +
                gameinsideNewsPost.getTags();
    }

    //posting today games

    public void postTodayGames(List<Match> matches) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageForTodayGames(matches))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatMessageForTodayGames(List<Match> matches) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\uD83D\uDCFA Матчі українських команд на ")
                .append(getFormattedDate())
                .append("\n\n");
        for (Match match : matches) {
            stringBuilder.append("\uD83D\uDFE2 Матч: <b>")
                    .append(match.getTeamOne().getName())
                    .append("</b> vs ")
                    .append("<b>")
                    .append(match.getTeamTwo().getName())
                    .append("</b> (Bo")
                    .append(match.getFormat())
                    .append(")\n")
                    .append("\uD83C\uDFC6 Турнір: <b>")
                    .append(match.getTournament().getName())
                    .append("</b>\n")
                    .append("⏰ Початок: ")
                    .append(match.getTime())
                    .append("\n\n");
        }
        return stringBuilder.toString();
    }

    private String getFormattedDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("uk", "UA"));

        return currentDate.format(formatter);
    }

    //post started matches

    public void postStartedMatch(Match match) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageForStartedMatch(match))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatMessageForStartedMatch(Match match) {
        StringBuilder stringBuilder = new StringBuilder().append("⏰ Розпочинається матч: <b>")
                .append(match.getTeamOne().getName())
                .append("</b> vs <b>")
                .append(match.getTeamTwo().getName())
                .append("</b> (Bo")
                .append(match.getFormat())
                .append(")\n\uD83C\uDFC6 Турнір: <b>")
                .append(match.getTournament().getName())
                .append("</b>\n");
        if (Objects.nonNull(match.getTeamOne().getPlayers()) && Objects.nonNull(match.getTeamTwo().getPlayers())) {
            stringBuilder.append("\uD83D\uDC65 Склади команд\n<b>")
                    .append(match.getTeamOne().getName())
                    .append("</b>: ")
                    .append(String.join(", ", match.getTeamOne().getPlayers()))
                    .append("\n<b>")
                    .append(match.getTeamTwo().getName())
                    .append("</b>: ")
                    .append(String.join(", ", match.getTeamTwo().getPlayers()));
        }
        return stringBuilder.toString();
    }

    //post game result

    public void postUkrainianTeamWonGame(Match match, Team team) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageUkrTeamWonGame(match, team))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void postTwoUkrainianTeamsGame(Match match, Team team) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTwoUkrTeamsGame(match, team))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void postUkrainianTeamLostGame(Match match, Team team) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageUkrTeamLostGame(match, team))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatMessageUkrTeamWonGame(Match match, Team team) {
        int gameNumber = match.getTeamOne().getScore() + match.getTeamTwo().getScore();
        return "<b>" +
                team.getName() +
                "</b>" +
                " перемогли <b>" +
                (match.getTeamOne().getName().equals(team.getName()) ? match.getTeamTwo().getName() : match.getTeamOne().getName()) +
                "</b> на " +
                gameNumber +
                " карті\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    private String formatMessageUkrTeamLostGame(Match match, Team team) {
        int gameNumber = match.getTeamOne().getScore() + match.getTeamTwo().getScore();
        return "<b>" +
                (match.getTeamOne().getName().equals(team.getName()) ? match.getTeamTwo().getName() : match.getTeamOne().getName()) +
                "</b>" +
                " програли <b>" +
                team.getName() +
                "</b> на " +
                gameNumber +
                " карті\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    private String formatMessageTwoUkrTeamsGame(Match match, Team team) {
        int gameNumber = match.getTeamOne().getScore() + match.getTeamTwo().getScore();
        return "<b>" +
                team.getName() +
                "</b>" +
                " перемогли <b>" +
                (match.getTeamOne().getName().equals(team.getName()) ? match.getTeamTwo().getName() : match.getTeamOne().getName()) +
                "</b> на " +
                gameNumber +
                " карті\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    //post match result

    public void postUkrainianTeamWonMatch(Match match, Team team) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageUkrTeamWonMatch(match, team))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void postUkrainianTeamLostMatch(Match match, Team team) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageUkrTeamLostMatch(match, team))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void postTwoUkrainianTeamsMatch(Match match) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageUkrTeamsMatch(match))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatMessageUkrTeamWonMatch(Match match, Team team) {
        return "<b>" +
                team.getName() +
                "</b>" +
                " перемогли <b>" +
                (match.getTeamOne().getName().equals(team.getName()) ? match.getTeamTwo().getName() : match.getTeamOne().getName()) +
                "</b> на " +
                match.getTournament().getName() +
                "\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    private String formatMessageUkrTeamLostMatch(Match match, Team team) {
        return "На жаль <b>" +
                (match.getTeamOne().getName().equals(team.getName()) ? match.getTeamTwo().getName() : match.getTeamOne().getName()) +
                "</b>" +
                " програли <b>" +
                team.getName() +
                "</b> на " +
                match.getTournament().getName() +
                "\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    private String formatMessageUkrTeamsMatch(Match match) {
        return "<b>" +
                match.getTeamOne().getName() +
                "</b>" +
                " перемогли <b>" +
                match.getTeamTwo().getName() +
                "</b> на " +
                match.getTournament().getName() +
                "\n\n<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b>";
    }

    // post day results

    public void postDayResults(List<Match> matches) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatDayResults(matches))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatDayResults(List<Match> matches) {
        StringBuilder s = new StringBuilder()
                .append("\uD83D\uDCDD Результати сьогоднішніх матчів за участі українських команд:\n\n");
        matches.forEach(match -> s.append(formatMatchResult(match)).append("\n\n"));
        return s.toString();
    }

    private String formatMatchResult(Match match) {
        return matchResultEmoji(match) +
                " " +
                "<b>" +
                match.getTeamOne().getName() +
                " " +
                match.getTeamOne().getScore() +
                " - " +
                match.getTeamTwo().getScore() +
                " " +
                match.getTeamTwo().getName() +
                "</b> " +
                "на " +
                match.getTournament().getName();
    }

    private String matchResultEmoji(Match match) {
        List<String> ukrTeams = ukrainianTeamService.getUkrainianTeams().stream()
                .map(UkrainianTeam::getName)
                .toList();
        if (ukrTeams.contains(match.getTeamOne().getName()) && ukrTeams.contains(match.getTeamTwo().getName()) ||
                match.getTeamOne().getScore() == match.getTeamTwo().getScore())
            return "\uD83D\uDFE0";
        if (match.getTeamOne().getScore() > match.getTeamTwo().getScore()) {
            if (ukrTeams.contains(match.getTeamOne().getName()))
                return "\uD83D\uDFE2";
            return "\uD83D\uDD34";
        } else {
            if (ukrTeams.contains(match.getTeamTwo().getName()))
                return "\uD83D\uDFE2";
            return "\uD83D\uDD34";
        }
    }

    //post transfers

    public void sendMessageTransferNoneToTeam(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTransferNoneToTeam(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageTransferNoneToTeamCoach(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTransferNoneToTeamCoach(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageTransferFromTeamToNone(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTransferFromTeamToNone(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageTransferRetiredToTeam(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTransferRetiredToTeam(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageTransferRetiredToTeamCoach(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageTransferRetiredToTeamCoach(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageFromTeamToInactive(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageFromTeamToInactive(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    public void sendMessageServiceFromTeamToTeam(Transfer transfer) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatMessageServiceFromTeamToTeam(transfer))
                .parseMode(ParseMode.HTML)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatMessageTransferNoneToTeam(Transfer transfer) {
        if (transfer.getPlayers().size() == 1) {
            return transfer.getPlayers().get(0).getFlag() +
                    "<b>" +
                    transfer.getPlayers().get(0).getName() +
                    "</b> приєднався до " +
                    transfer.getNewTeam() +
                    "\n" +
                    transfer.getNewsLink();
        } else {
            StringBuilder s = new StringBuilder(transfer.getPlayers().get(0).getFlag() +
                    "<b>" + transfer.getPlayers().get(0).getName() + "</b>");
            for (int w = 1; w < transfer.getPlayers().size() - 1; w++) {
                s.append(", ")
                        .append(transfer.getPlayers().get(w).getFlag())
                        .append("<b>")
                        .append(transfer.getPlayers().get(w).getName())
                        .append("</b>");
            }
            s.append(" та ")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getFlag())
                    .append("<b>")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getName())
                    .append("</b>");
            s.append(" приєднались до ")
                    .append(transfer.getNewTeam())
                    .append("\n")
                    .append(transfer.getNewsLink());
            return s.toString();
        }
    }

    private String formatMessageTransferNoneToTeamCoach(Transfer transfer) {
        return transfer.getPlayers().get(0).getFlag() +
                "<b>" +
                transfer.getPlayers().get(0).getName() +
                "</b> приєднався до " +
                transfer.getNewTeam() +
                " на позицію тренера\n" +
                transfer.getNewsLink();
    }

    private String formatMessageTransferFromTeamToNone(Transfer transfer) {
        if (transfer.getPlayers().size() == 1) {
            return transfer.getPlayers().get(0).getFlag() +
                    "<b>" +
                    transfer.getPlayers().get(0).getName() +
                    "</b> покинув склад " +
                    transfer.getOldTeam() +
                    "\n" +
                    transfer.getNewsLink();
        } else {
            StringBuilder s = new StringBuilder(transfer.getPlayers().get(0).getFlag() +
                    "<b>" + transfer.getPlayers().get(0).getName() + "</b>");
            for (int w = 0; w < transfer.getPlayers().size() - 1; w++) {
                s.append(", ")
                        .append(transfer.getPlayers().get(w).getFlag())
                        .append("<b>")
                        .append(transfer.getPlayers().get(w).getName())
                        .append("</b>");
            }
            s.append(" та ")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getFlag())
                    .append("<b>")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getName())
                    .append("</b>");
            s.append(" покинули склад ")
                    .append(transfer.getOldTeam())
                    .append("\n")
                    .append(transfer.getNewsLink());
            return s.toString();
        }
    }

    private String formatMessageTransferRetiredToTeam(Transfer transfer) {
        return transfer.getPlayers().get(0).getFlag() +
                "<b>" +
                transfer.getPlayers().get(0).getName() +
                "</b> повернувся на професійну сцену та приєднався до " +
                transfer.getNewTeam() +
                "\n" +
                transfer.getNewsLink();
    }

    private String formatMessageTransferRetiredToTeamCoach(Transfer transfer) {
        return transfer.getPlayers().get(0).getFlag() +
                "<b>" +
                transfer.getPlayers().get(0).getName() +
                "</b> повернувся на професійну сцену та приєднався до " +
                transfer.getNewTeam() +
                " на позицію тренера\n" +
                transfer.getNewsLink();
    }

    private String formatMessageFromTeamToInactive(Transfer transfer) {
        if (transfer.getPlayers().size() == 1) {
            return transfer.getOldTeam() +
                    " перевели " +
                    transfer.getPlayers().get(0).getFlag() +
                    "<b>" +
                    transfer.getPlayers().get(0).getName() +
                    "</b> на лаву запасних\n" +
                    transfer.getNewsLink();
        } else {
            StringBuilder s = new StringBuilder(transfer.getOldTeam() + " перевели ");
            for (int w = 1; w < transfer.getPlayers().size() - 1; w++) {
                s.append(", ")
                        .append(transfer.getPlayers().get(w).getFlag())
                        .append("<b>")
                        .append(transfer.getPlayers().get(w).getName())
                        .append("</b>");
            }
            s.append(" та ")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getFlag())
                    .append("<b>")
                    .append(transfer.getPlayers().get(transfer.getPlayers().size() - 1).getName())
                    .append("</b>");
            s.append(" на лаву запасних\n ")
                    .append(transfer.getNewsLink());
            return s.toString();
        }
    }

    private String formatMessageServiceFromTeamToTeam(Transfer transfer) {
        return transfer.getPlayers().get(0).getFlag() +
                "<b>" +
                transfer.getPlayers().get(0).getName() +
                "</b> перейшов з " +
                transfer.getOldTeam() +
                " до " +
                transfer.getNewTeam() +
                "\n" +
                transfer.getNewsLink();
    }

    public void sendThisDayInDotaMessage(DayInDota dayInDota) {
        var message = SendMessage.builder()
                .chatId(CHAT_ID)
                .text(formatThisDayInDotaMessage(dayInDota))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();

        messageSender.sendMessage(message);
    }

    private String formatThisDayInDotaMessage(DayInDota dayInDota) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Цього дня в Dota 2</b>\n");
        if (dayInDota.getTournamentWinners().size() > 0) {
            dayInDota.getTournamentWinners().forEach((key, value) -> stringBuilder.append("\n◻\uFE0F <b>")
                    .append(key.getYear())
                    .append("</b>\n\uD83C\uDFC6 <b><a href=\"")
                    .append(value.getLink())
                    .append("\">")
                    .append(value.getName())
                    .append("</a></b> виграли на <b><a href=\"")
                    .append(key.getLink())
                    .append("\">")
                    .append(key.getName())
                    .append("</a></b>\n"));
        }

        if(dayInDota.getPlayersBirths().size() > 0) {
            stringBuilder.append("\n\uD83C\uDF88<b> Сьогодні день народження ")
                    .append(dayInDota.getPlayersBirths().size() > 1 ? "святкують:</b>\n" : "святкує:</b>\n");
            for (Player player : dayInDota.getPlayersBirths()) {
                stringBuilder.append("\uD83D\uDD38 <a href=\"")
                        .append(player.getLink())
                        .append("\">")
                        .append(player.getName())
                        .append("</a> - ")
                        .append(player.getYearOfBirth())
                        .append(" (")
                        .append(getAge(player.getYearOfBirth()))
                        .append(")\n");
            }
        }

        return stringBuilder.toString();
    }

    private String getAge(String year) {
        LocalDate age = LocalDate.now().minusYears(Long.parseLong(year));
        return String.valueOf(age.getYear());
    }

}
