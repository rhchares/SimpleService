CREATE INDEX IF NOT EXISTS IDX_USERS_ID_DESC_COVER
ON public.users (id DESC)
INCLUDE (username, email);

CREATE INDEX IF NOT exists comments_post_created_id
ON public.comments (post_id, parent_id, created_at DESC)
INCLUDE (id);

CREATE INDEX IF NOT exists comments_reply_created_id
ON public.comments (parent_id, created_at DESC)
INCLUDE (id);

CREATE INDEX IF NOT EXISTS idx_comments_parent_count
ON public.comments (parent_id);

CREATE INDEX IF NOT EXISTS idx_comments_post_parent_count
ON public.comments (post_id, parent_id);

