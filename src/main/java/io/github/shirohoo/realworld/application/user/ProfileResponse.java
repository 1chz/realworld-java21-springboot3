package io.github.shirohoo.realworld.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("profile")
record ProfileResponse(String username, String bio, String image, boolean following) {}
