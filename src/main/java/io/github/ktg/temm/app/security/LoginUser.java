package io.github.ktg.temm.app.security;

import java.util.List;
import java.util.UUID;

public record LoginUser(UUID userId, List<LoginUserStore> storeInfos) {

}
