INSERT INTO privileges (id, modified, created, name) VALUES
  (1, NOW(), NOW(), 'ORG_READ'),
  (2, NOW(), NOW(), 'ORG_EDIT'),
  (3, NOW(), NOW(), 'ORG_UPDATE_PERMISSIONS'),
  (4, NOW(), NOW(), 'ORG_READ_PERMISSIONS'),
  (5, NOW(), NOW(), 'ORG_CREATE_PROJECT'),
  (6, NOW(), NOW(), 'ORG_DELETE'),
  (7, NOW(), NOW(), 'PROJECT_READ'),
  (8, NOW(), NOW(), 'PROJECT_EDIT'),
  (9, NOW(), NOW(), 'PROJECT_READ_COMMITS'),
  (10, NOW(), NOW(), 'PROJECT_CREATE_BRANCH'),
  (11, NOW(), NOW(), 'PROJECT_DELETE'),
  (12, NOW(), NOW(), 'PROJECT_UPDATE_PERMISSIONS'),
  (13, NOW(), NOW(), 'PROJECT_READ_PERMISSIONS'),
  (14, NOW(), NOW(), 'PROJECT_CREATE_WEBHOOKS'),
  (15, NOW(), NOW(), 'BRANCH_READ'),
  (16, NOW(), NOW(), 'BRANCH_EDIT_CONTENT'),
  (17, NOW(), NOW(), 'BRANCH_DELETE'),
  (18, NOW(), NOW(), 'BRANCH_UPDATE_PERMISSIONS'),
  (19, NOW(), NOW(), 'BRANCH_READ_PERMISSIONS')
  ON CONFLICT DO NOTHING;

INSERT INTO roles (id, modified, created, name) VALUES
  (0, NOW(), NOW(), 'ADMIN'),
  (1, NOW(), NOW(), 'READER'),
  (2, NOW(), NOW(), 'WRITER')
  ON CONFLICT DO NOTHING;

INSERT INTO roles_privileges (role_id, privilege_id) VALUES
  (0, 1),
  (0, 2),
  (0, 3),
  (0, 4),
  (0, 5),
  (0, 6),
  (0, 7),
  (0, 8),
  (0, 9),
  (0, 10),
  (0, 11),
  (0, 12),
  (0, 13),
  (0, 14),
  (0, 15),
  (0, 16),
  (0, 17),
  (0, 18),
  (0, 19),
  (2, 1),
  (2, 2),
  (2, 4),
  (2, 5),
  (2, 7),
  (2, 8),
  (2, 9),
  (2, 10),
  (2, 13),
  (2, 14),
  (2, 15),
  (2, 16),
  (2, 19),
  (1, 1),
  (1, 4),
  (1, 7),
  (1, 9),
  (1, 13),
  (1, 15),
  (1, 19)
  ON CONFLICT DO NOTHING;

INSERT INTO groups (id, created, modified, name) VALUES
  (0, NOW(), NOW(), 'everyone')
  ON CONFLICT DO NOTHING;