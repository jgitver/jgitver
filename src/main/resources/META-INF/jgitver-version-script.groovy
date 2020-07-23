def currentPatch = metadata.CURRENT_VERSION_PATCH

// autoIncrementPatch by default
def patch = (metadata.BASE_COMMIT_ON_HEAD && metadata.ANNOTATE) ? currentPatch + 1 : currentPatch

def mmp = metadata.CURRENT_VERSION_MAJOR + ';' + metadata.CURRENT_VERSION_MINOR + ';' + patch

def qualifiers = []

if (!metadata.DETACHED_HEAD && metadata.QUALIFIED_BRANCH_NAME) {
  qualifiers.add(metadata.QUALIFIED_BRANCH_NAME)
}

if (!metadata.BASE_COMMIT_ON_HEAD || !metadata.ANNOTATED) {
  def sz = qualifiers.size()

  if (sz == 0) {
    qualifiers.add(metadata.COMMIT_DISTANCE)
  } else {
    qualifiers[sz-1] = qualifiers[sz-1] + '.' + metadata.COMMIT_DISTANCE
  }
}

print mmp + ';' + qualifiers.join(';')