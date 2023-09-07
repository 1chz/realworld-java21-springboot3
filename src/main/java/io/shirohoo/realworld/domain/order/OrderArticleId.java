package io.shirohoo.realworld.domain.order;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderArticleId implements Serializable {
    private Integer orderId;
    private Integer articleId;
}
