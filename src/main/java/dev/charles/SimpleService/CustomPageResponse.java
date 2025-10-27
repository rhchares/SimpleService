package dev.charles.SimpleService;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CustomPageResponse<T> {
    private final List<T> content;
    private final PageMetadata page;

    // Page<T> 객체를 인자로 받아 필요한 필드만 추출하여 설정합니다.
    public CustomPageResponse(Page<T> pageResult) {
        this.content = pageResult.getContent();
        this.page = new PageMetadata(pageResult);
    }

    @Getter
    private static class PageMetadata {
        private final long totalElements;
        private final int totalPages;
        private final int size;
        private final int number;

        public PageMetadata(Page<?> pageResult) {
            this.totalElements = pageResult.getTotalElements();
            this.totalPages = pageResult.getTotalPages();
            this.size = pageResult.getSize();
            this.number = pageResult.getNumber(); // 현재 페이지 번호 (0부터 시작)
        }
    }
}