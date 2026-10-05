package es.us.ieal.search.infra.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("contents")
@CompoundIndex(name = "source_unique", def = "{'sourceSystem': 1, 'sourceId': 1}", unique = true)
public class ContentDocument {
    @Id
    private String id;
    private String sourceId;
    private String sourceSystem;
    private String sourceUrl;
    private String slug;
    @TextIndexed(weight = 5)
    private String title;
    @TextIndexed(weight = 3)
    private String summary;
    @TextIndexed
    private String body;
    private String contentType;
    private String section;
    private List<String> categories;
    private List<String> tags;
    private List<String> keywords;
    private String language;
    private Instant publishedAt;
    private Instant lastUpdatedAt;
    private Instant indexedAt;
    private Instant eventStartAt;
    private Instant eventEndAt;
    private String location;
    private String organizer;
    private String status;
    private boolean visible;
    private String searchTextNormalized;

    public ContentDocument() {
    }

    public ContentDocument(String id, String sourceId, String sourceSystem, String sourceUrl,
                           String slug, String title, String summary, String body,
                           String contentType, String section, List<String> categories,
                           List<String> tags, List<String> keywords, String language,
                           Instant publishedAt, Instant lastUpdatedAt,
                           Instant indexedAt, Instant eventStartAt,
                           Instant eventEndAt, String location, String organizer,
                           String status, boolean visible,
                           String searchTextNormalized) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceSystem = sourceSystem;
        this.sourceUrl = sourceUrl;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.contentType = contentType;
        this.section = section;
        this.categories = categories;
        this.tags = tags;
        this.keywords = keywords;
        this.language = language;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.indexedAt = indexedAt;
        this.eventStartAt = eventStartAt;
        this.eventEndAt = eventEndAt;
        this.location = location;
        this.organizer = organizer;
        this.status = status;
        this.visible = visible;
        this.searchTextNormalized = searchTextNormalized;
    }

    public String getId() { return id; }
    public String getSourceId() { return sourceId; }
    public String getSourceSystem() { return sourceSystem; }
    public String getSourceUrl() { return sourceUrl; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getBody() { return body; }
    public String getContentType() { return contentType; }
    public String getSection() { return section; }
    public List<String> getCategories() { return categories; }
    public List<String> getTags() { return tags; }
    public List<String> getKeywords() { return keywords; }
    public String getLanguage() { return language; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public Instant getIndexedAt() { return indexedAt; }
    public Instant getEventStartAt() { return eventStartAt; }
    public Instant getEventEndAt() { return eventEndAt; }
    public String getLocation() { return location; }
    public String getOrganizer() { return organizer; }
    public String getStatus() { return status; }
    public boolean isVisible() { return visible; }
    public String getSearchTextNormalized() { return searchTextNormalized; }
}
