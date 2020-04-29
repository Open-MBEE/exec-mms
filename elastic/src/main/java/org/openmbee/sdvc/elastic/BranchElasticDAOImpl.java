package org.openmbee.sdvc.elastic;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.get.GetResult;
import org.openmbee.sdvc.core.dao.BranchIndexDAO;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.RefJson;
import org.springframework.stereotype.Component;

@Component
public class BranchElasticDAOImpl extends BaseElasticDAOImpl<RefJson> implements BranchIndexDAO {

    protected RefJson newInstance() {
        return new RefJson();
    }

    @Override
    public void indexAll(Collection<? extends BaseJson> jsons) {
        this.indexAll(jsons);
    }

    @Override
    public void index(BaseJson json) {
        this.index(json);
    }

    public Optional<RefJson> findById(String docId) {
        return this.findById(getIndex(), docId);
    }

    public List<RefJson> findAllById(Set<String> docIds) {
        return this.findAllById(getIndex(), docIds);
    }

    public void deleteById(String docId) {
        this.deleteById(getIndex(), docId);
    }

    public void deleteAll(Collection<? extends BaseJson> jsons) {
        this.deleteAll(getIndex(), jsons);
    }

    public boolean existsById(String docId) {
        return this.existsById(getIndex(), docId);
    }

    @Override
    public void update(RefJson refJson) {
        try {
            UpdateRequest request = new UpdateRequest(getIndex(), refJson.getDocId());
            request.fetchSource(true);
            request.docAsUpsert(true).doc(refJson);
            RefJson response = new RefJson();

            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
            if (updateResponse.getResult() == DocWriteResponse.Result.CREATED ||
                updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                GetResult result = updateResponse.getGetResult();
                if (result.isExists()) {
                    response.putAll(result.sourceAsMap());
                }
            }
            // TODO: Handle other getResults maybe
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getIndex() {
        return super.getIndex() + "_commit";
    }
}

