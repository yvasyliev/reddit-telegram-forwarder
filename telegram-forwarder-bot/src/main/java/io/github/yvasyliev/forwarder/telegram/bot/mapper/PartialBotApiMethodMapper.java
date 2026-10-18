package io.github.yvasyliev.forwarder.telegram.bot.mapper;

import io.github.yvasyliev.forwarder.telegram.core.configuration.TelegramAdminProperties;
import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendAnimationDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMediaGroupDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendPhotoDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendVideoDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

import java.util.Optional;
import java.util.SequencedCollection;

/**
 * Mapper interface for converting between {@link PartialBotApiMethodDTO} and {@link PartialBotApiMethod}.
 */
@Mapper(uses = {InputFileMapper.class, InputMediaMapper.class})
public interface PartialBotApiMethodMapper {
    /**
     * Maps a {@link PartialBotApiMethodDTO} to a {@link PartialBotApiMethod} using the provided
     * {@link TelegramAdminProperties}.
     *
     * @param methodDTO       the source {@link PartialBotApiMethodDTO}
     * @param adminProperties the {@link TelegramAdminProperties} containing the chat ID
     * @return the mapped {@link PartialBotApiMethod}
     */
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    @SubclassMapping(target = SendAnimation.class, source = SendAnimationDTO.class)
    @SubclassMapping(target = SendMediaGroup.class, source = SendMediaGroupDTO.class)
    @SubclassMapping(target = SendMessage.class, source = SendMessageDTO.class)
    @SubclassMapping(target = SendPhoto.class, source = SendPhotoDTO.class)
    @SubclassMapping(target = SendVideo.class, source = SendVideoDTO.class)
    @Mapping(target = "chatId", expression = "java(adminProperties.id())")
    PartialBotApiMethod<?> map(PartialBotApiMethodDTO methodDTO, @Context TelegramAdminProperties adminProperties);

    /**
     * Maps a {@link SendMessageDTO} to a {@link SendMessage} using the provided {@link TelegramAdminProperties}.
     *
     * @param sendMessageDTO  the source {@link SendMessageDTO}
     * @param adminProperties the {@link TelegramAdminProperties} containing the chat ID
     * @return the mapped {@link SendMessage}
     */
    @Mapping(target = "parseMode", constant = ParseMode.HTML)
    @Mapping(target = "chatId", expression = "java(adminProperties.id())")
    SendMessage map(SendMessageDTO sendMessageDTO, @Context TelegramAdminProperties adminProperties);

    /**
     * Sets the caption for the first media in the {@link SendMediaGroup} after mapping.
     *
     * @param sendMediaGroup    the mapped {@link SendMediaGroup}
     * @param sendMediaGroupDTO the source {@link SendMediaGroupDTO}
     */
    @AfterMapping
    default void setCaption(@MappingTarget SendMediaGroup sendMediaGroup, SendMediaGroupDTO sendMediaGroupDTO) {
        Optional.of(sendMediaGroup.getMedias())
                .filter(medias -> !medias.isEmpty())
                .map(SequencedCollection::getFirst)
                .ifPresent(media -> media.setCaption(sendMediaGroupDTO.caption()));
    }
}
