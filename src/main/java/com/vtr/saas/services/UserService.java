package com.vtr.saas.services;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.requests.UserRequest;
import com.vtr.saas.responses.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void createUser(final UserRequest request);

    void updatedUser(final String id, final UserRequest request);

    void deleteUser(final String id);

    UserResponse getUserById(final String userId);

    PageResponse<UserResponse> getAllUsers(final int page, final int size);

    void enableUser(final String userId);

    void disableUser(final String userId);
}
