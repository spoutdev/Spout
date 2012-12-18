import bpy;
from bpy_extras.io_utils import ExportHelper;

bl_info = {
    "name":         "Spout Skeleton",
    "author":       "Karang",
    "blender":      (2,6,2),
    "version":      (0,0,1),
    "location":     "File > Import-Export",
    "description":  "Export skeleton for spout engine.",
    "category":     "Import-Export"
}

class ExportSKE(bpy.types.Operator, ExportHelper):
    bl_idname       = "export_bones.ske";
    bl_label        = "Spout Animation Model";
    bl_options      = {'PRESET'};

    filename_ext    = ".ske";

    def saveSKE(self, context, path):
        f = open(path, "w", encoding="utf8", newline="\n");

        obj = context.object;
        armature = obj.parent.data;

        vgroups = {};
        for group in obj.vertex_groups:
            vgroups[group.name] = [];

        for v in obj.data.vertices:
            for group in v.groups:
                vgroups[obj.vertex_groups[group.group].name].append(v.index);

        children = {None: []};
        for bone in armature.bones:
            children[bone.name] = [];
        for bone in armature.bones:
            children[getattr(bone.parent, "name", None)].append(bone.name);

        def writeBones(bone, indent):
            indent_str = (indent*4) * " ";
            f.write(indent_str + bone + ":\n");
            indent_str += "    ";

            # Children
            if (children[bone]):
                f.write(indent_str + "children:\n");
                for child in children[bone]:
                    writeBones(child, indent+2);

            # Vertices & Weight
            if (vgroups[bone]):
                f.write(indent_str + "vertices:");
                for v in vgroups[bone]:
                    f.write(" " + str(v));
                f.write("\n");
                f.write(indent_str + "weight:");
                for v in vgroups[bone]:
                    f.write(" %.4f" % obj.vertex_groups[bone].weight(v));
                f.write("\n");

        writeBones(children[None][0], 0);

        f.close();
        return {'FINISHED'};

    def execute(self, context):
        path = self.as_keywords()["filepath"];
        return self.saveSKE(context, path);

def menu_func(self, context):
    self.layout.operator(ExportSKE.bl_idname, text="Spout Skeleton (.ske)");

def register():
    bpy.utils.register_module(__name__);
    bpy.types.INFO_MT_file_export.append(menu_func);

def unregister():
    bpy.utils.unregister_module(__name__);
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()
