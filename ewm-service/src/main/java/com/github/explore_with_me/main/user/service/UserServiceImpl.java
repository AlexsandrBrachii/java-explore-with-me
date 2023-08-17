package com.github.explore_with_me.main.user.service;

import java.util.ArrayList;
import java.util.List;

import com.github.explore_with_me.main.exception.model.ConflictException;
import com.github.explore_with_me.main.user.dto.UserDto;
import com.github.explore_with_me.main.user.mapper.UserMapper;
import com.github.explore_with_me.main.user.model.User;
import com.github.explore_with_me.main.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id " + userId + "удалён");
    }

    @Override
    public List<UserDto> getUsersInfo(List<Long> ids, int from, int size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        List<User> all = new ArrayList<>();
        if (ids == null) {
            all.addAll(userRepository.findAll(pagination).getContent());
        } else {
            all.addAll(userRepository.findAllByIdIn(ids, pagination));
        }
        log.info("Получены все пользователи " + all);
        return userMapper.userListToUserDtoList(all);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            catchSqlException(e);
        }
        log.info("Пользователь= " + user + " создан.");
        return userMapper.userToUserDto(user);
    }

    private void catchSqlException(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getCause().getCause().getMessage());
        int indexEqualsSign = stringBuilder.indexOf("=");
        stringBuilder.delete(0, indexEqualsSign + 1);
        throw new ConflictException(stringBuilder.toString().replace("\"", "").trim());
    }
}

