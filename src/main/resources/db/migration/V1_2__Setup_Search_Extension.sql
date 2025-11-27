CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_users_username_trgm
ON users USING gin (username gin_trgm_ops);

ALTER TABLE posts
ADD COLUMN IF NOT EXISTS post_tsv TSVECTOR
GENERATED ALWAYS AS (
    strip(to_tsvector('english', posts.title || ' ' || posts.content))
) STORED;

CREATE INDEX idx_fts_post ON posts USING GIN
(post_tsv);
