CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    name VARCHAR(200) NOT NULL,
    avatar_url TEXT,
    role VARCHAR(20) NOT NULL CHECK (role IN ('BREEDER', 'BUYER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cat_females (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    birth_date DATE NOT NULL,
    mating_date DATE,
    birth_due_date DATE,
    photo_urls TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cat_males (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    birth_date DATE NOT NULL,
    photo_urls TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS litters (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(10) NOT NULL,
    birth_date DATE NOT NULL,
    total_count INTEGER NOT NULL DEFAULT 0,
    male_count INTEGER NOT NULL DEFAULT 0,
    female_count INTEGER NOT NULL DEFAULT 0,
    mother_id INTEGER REFERENCES cat_females(id) ON DELETE SET NULL,
    father_id INTEGER REFERENCES cat_males(id) ON DELETE SET NULL,
    photo_urls TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (owner_id, name)
);

CREATE TABLE IF NOT EXISTS kittens (
    id SERIAL PRIMARY KEY,
    litter_id INTEGER NOT NULL REFERENCES litters(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    birth_date DATE NOT NULL,
    color VARCHAR(200) NOT NULL,
    birth_weight DECIMAL(5,2),
    status VARCHAR(20) NOT NULL DEFAULT 'FREE' CHECK (status IN ('FREE', 'RESERVED')),
    photo_urls TEXT NOT NULL DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    kitten_id INTEGER NOT NULL REFERENCES kittens(id) ON DELETE CASCADE,
    buyer_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_cat_females_owner ON cat_females(owner_id);
CREATE INDEX IF NOT EXISTS idx_cat_males_owner ON cat_males(owner_id);
CREATE INDEX IF NOT EXISTS idx_litters_owner ON litters(owner_id);
CREATE INDEX IF NOT EXISTS idx_kittens_litter ON kittens(litter_id);
CREATE INDEX IF NOT EXISTS idx_kittens_status ON kittens(status);
CREATE INDEX IF NOT EXISTS idx_reservations_buyer ON reservations(buyer_id);
CREATE INDEX IF NOT EXISTS idx_reservations_kitten ON reservations(kitten_id);
